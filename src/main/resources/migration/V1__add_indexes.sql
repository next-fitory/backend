-- =============================================================
-- V1__add_indexes.sql
-- =============================================================


-- ---------------------------------------------------------------
-- [users]
-- ---------------------------------------------------------------

-- JwtSecurityFilter: 토큰에서 추출한 userId로 findById 호출 → 모든 인증 요청마다 실행됨
-- AuthService.login: 로그인 시 findByEmail 호출 → 이메일 중복 검사도 포함
-- email 은 로그인/회원가입 양쪽에서 조회되며 유니크 제약도 겸함
CREATE UNIQUE INDEX idx_users_email
    ON users (email);


-- ---------------------------------------------------------------
-- [brands]
-- ---------------------------------------------------------------

-- BrandService.findByName: 브랜드명 중복 검사 시 사용
-- 브랜드명은 비즈니스적으로 유니크해야 하므로 유니크 인덱스로 제약 + 조회 성능 동시 확보
CREATE UNIQUE INDEX idx_brands_name
    ON brands (name);


-- ---------------------------------------------------------------
-- [categories]
-- ---------------------------------------------------------------

-- 상품 검색 쿼리에서 products JOIN categories ON p.category_id = c.id WHERE c.slug = ?
-- slug 는 프론트에서 ':outer' 형태로 전달되는 검색 필터의 핵심 컬럼
-- slug 도 유니크해야 하므로 유니크 인덱스로 제약 + JOIN 필터 성능 확보
CREATE UNIQUE INDEX idx_categories_slug
    ON categories (slug);


-- ---------------------------------------------------------------
-- [products]
-- ---------------------------------------------------------------

-- ProductRepository.findAll / findAllByOrderByCreatedAtDesc:
--   WHERE deleted = false ORDER BY created_at DESC
--   deleted 로 먼저 필터링하고 created_at 으로 정렬하므로 복합 인덱스가 정렬 비용을 제거함
--   deleted = false 인 행만 읽기 때문에 인덱스 스캔 범위가 크게 줄어듦
CREATE INDEX idx_products_deleted_created_at
    ON products (deleted, created_at DESC);

-- ProductRepository.findAllByCategoryId:
--   WHERE category_id = ? AND deleted = false ORDER BY created_at DESC
--   category_id 로 먼저 좁히고, deleted 필터 후 최신순 정렬
--   partial index(WHERE deleted = false)로 삭제된 상품 제외 → 인덱스 크기 절감
CREATE INDEX idx_products_category_id
    ON products (category_id, created_at DESC)
    WHERE deleted = false;

-- ProductRepository.findAllByBrandId (페이징):
--   WHERE brand_id = ? AND deleted = false ORDER BY created_at DESC LIMIT ? OFFSET ?
--   brand_id 조건 → deleted 필터 → created_at 정렬을 인덱스 하나로 처리
--   LIMIT/OFFSET 페이징에서 filesort 없이 인덱스 순서 그대로 반환 가능
CREATE INDEX idx_products_brand_id
    ON products (brand_id, created_at DESC)
    WHERE deleted = false;

-- ProductRepository.search (브랜드 + 카테고리 복합 검색):
--   WHERE p.brand_id = ? AND p.category_id = ? AND p.deleted = false
--   두 FK 를 동시에 필터링하는 경우 각각의 단일 인덱스보다 복합 인덱스가 훨씬 효율적
--   브랜드 → 카테고리 순서: 브랜드 선택률이 낮아 먼저 좁히는 것이 유리
CREATE INDEX idx_products_brand_category
    ON products (brand_id, category_id)
    WHERE deleted = false;

-- ProductRepository.search (keyword LIKE '%...%'):
--   B-tree 인덱스는 전방 일치(keyword%)만 지원하고 후방·중간 일치는 full scan 발생
--   pg_trgm 은 문자열을 trigram(3글자 조각)으로 분해해 GIN 인덱스에 저장하므로
--   '%keyword%' 패턴도 인덱스 스캔으로 처리 가능 → 상품 수가 많아질수록 효과 극대화
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX idx_products_name_trgm
    ON products USING GIN (name gin_trgm_ops);


-- ---------------------------------------------------------------
-- [cart_items]
-- ---------------------------------------------------------------

-- CartItemRepository.findByUserIdAndProductId:
--   장바구니 담기 전 중복 여부 확인 시 (user_id, product_id) 로 단건 조회
--   유니크 인덱스로 DB 레벨에서 중복 방지 + 조회 성능을 동시에 확보
--   애플리케이션에서 체크해도 레이스 컨디션 가능성 있으므로 DB 제약이 안전
CREATE UNIQUE INDEX idx_cart_items_user_product
    ON cart_items (user_id, product_id);

-- CartItemRepository.findCartProductsByUserId (페이징):
--   WHERE ci.user_id = ? AND p.deleted = false ORDER BY ci.created_at DESC LIMIT ? OFFSET ?
--   user_id 로 좁히고 created_at 정렬을 인덱스로 처리 → filesort 제거
--   deleteAllByUserId 도 user_id 스캔이므로 이 인덱스가 함께 활용됨
CREATE INDEX idx_cart_items_user_id_created_at
    ON cart_items (user_id, created_at DESC);


-- ---------------------------------------------------------------
-- [reviews]
-- ---------------------------------------------------------------

-- ReviewRepository.findAllByProductId / countByProductId:
--   WHERE product_id = ? AND deleted = false ORDER BY created_at DESC LIMIT ? OFFSET ?
--   partial index(WHERE deleted = false)로 실제 노출되는 리뷰만 인덱스에 포함
--   countByProductId 도 같은 조건이므로 이 인덱스로 Index Only Scan 가능
CREATE INDEX idx_reviews_product_id_created_at
    ON reviews (product_id, created_at DESC)
    WHERE deleted = false;

-- ReviewRepository.findAll (전체 리뷰 페이징):
--   WHERE deleted = false ORDER BY created_at DESC LIMIT ? OFFSET ?
--   deleted 로 먼저 필터링 후 정렬 → filesort 없이 인덱스 순서 그대로 반환
CREATE INDEX idx_reviews_deleted_created_at
    ON reviews (deleted, created_at DESC);


-- ---------------------------------------------------------------
-- [orders]
-- ---------------------------------------------------------------

-- OrderRepository.findByUserId:
--   WHERE user_id = ? ORDER BY created_at DESC
--   주문 내역 조회는 마이페이지에서 자주 호출되는 경로
CREATE INDEX idx_orders_user_id
    ON orders (user_id, created_at DESC);

-- OrderRepository.findByStatus (관리자 주문 관리):
--   WHERE status = 'PENDING' 등 상태별 조회 시 전체 테이블 스캔 방지
--   status 카디널리티가 낮아도 대량 주문 환경에서는 partial scan 효과가 있음
CREATE INDEX idx_orders_status
    ON orders (status);


-- ---------------------------------------------------------------
-- [order_items]
-- ---------------------------------------------------------------

-- OrderItemRepository.findByOrderId:
--   WHERE order_id = ? → 주문 상세 조회 시 반드시 사용됨
--   1:N 관계에서 FK 컬럼에 인덱스가 없으면 주문 건수만큼 sequential scan 발생
CREATE INDEX idx_order_items_order_id
    ON order_items (order_id);

-- OrderItemRepository.findByProductId:
--   WHERE product_id = ? → 상품별 판매 내역·통계 조회 시 사용
CREATE INDEX idx_order_items_product_id
    ON order_items (product_id);


-- ---------------------------------------------------------------
-- [users_likes]
-- ---------------------------------------------------------------

-- UserLikeRepository.findByUserIdAndProductId:
--   좋아요 토글 시 중복 여부 확인 + 삭제(deleteByUserIdAndProductId)에서 사용
--   유니크 인덱스로 동시 좋아요 요청의 레이스 컨디션을 DB 레벨에서 차단
CREATE UNIQUE INDEX idx_users_likes_user_product
    ON users_likes (user_id, product_id);

-- UserLikeRepository.findAllByProductId:
--   WHERE product_id = ? ORDER BY created_at DESC
--   상품 상세에서 좋아요한 유저 목록 또는 좋아요 수 집계 시 사용
--   findAllByUserId 는 위의 유니크 인덱스 (user_id, product_id) 로 커버됨
CREATE INDEX idx_users_likes_product_id_created_at
    ON users_likes (product_id, created_at DESC);


-- ---------------------------------------------------------------
-- [new_arrivals]
-- ---------------------------------------------------------------

-- ProductCurationRepository.findNewArrivals:
--   WHERE na.deleted = false AND p.deleted = false ORDER BY na.created_at DESC
--   deleted 로 필터링 후 최신순 정렬을 인덱스로 처리
CREATE INDEX idx_new_arrivals_deleted_created_at
    ON new_arrivals (deleted, created_at DESC);


-- ---------------------------------------------------------------
-- [ranked_products]
-- ---------------------------------------------------------------

-- ProductCurationRepository.findRanked:
--   WHERE rp.deleted = false AND p.deleted = false ORDER BY rp.created_at DESC
--   new_arrivals 와 동일한 패턴 → 같은 근거로 인덱스 추가
CREATE INDEX idx_ranked_products_deleted_created_at
    ON ranked_products (deleted, created_at DESC);
