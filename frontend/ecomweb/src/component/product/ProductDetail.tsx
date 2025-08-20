import React, { useState, useEffect } from 'react';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Thumbs } from 'swiper/modules';
import type { Swiper as SwiperCore } from 'swiper';
import Header from "../layout/Header";
import Footer from "../layout/Footer";
import { useParams, useNavigate } from "react-router-dom";
import { getProductDetail, getProductAttributes, getProductReviews, getProductReviewStats, addToCart } from "../../api/api";
import StarRating from "./StarRating";
import ReviewProduct from "./ReviewProduct";
import { useAppDispatch } from '../../store/hooks';
import { setOrderShopGroups } from '../../store/features/orderSlice';
import { toast } from 'react-toastify';

// Import Swiper styles
import 'swiper/swiper-bundle.css';

interface ProductDetailResponse {
  id: string;
  name: string;
  description: string;
  price: number;
  weight?: number;
  length?: number;
  width?: number;
  height?: number;
  shop:{
    id: string;
    name: string;
  }
  images: Array<{
    id: string;
    url: string;
    isMain: boolean;
  }>;
  productVariants:Array<{
    id: string;
    price: number;
    stock: number;
    detailAttributes:Array<{
        id:string;
        name:string;
    }>
  }>
  rating: number;
  numberOfOrder: number;
  productAttributes:Array<{
    id: string;
    name: string;
    detailAttributes:Array<{
        id:string;
        name:string;
    }>
  }>
 
  // Thêm các trường khác nếu cần
}

interface ReviewStats {
  stat: { [key: string]: number };
  total: number;
  avrRating: number;
}

interface ReviewDetail {
  id: string;
  rating: number;
  comment: string;
  createdAt: string | null;
  user: {
    id: string;
    username: string;
    fullName: string;
  };
  productVariant: {
    id: string;
    price: number;
    stock: number;
    detailAttributes: { id: string; name: string }[];
  };
}

// interface ProductAttribute {
//   id: string;
//   name: string;
//   detailAttributes: Array<{
//     id: string;
//     name: string;
//   }>;
// }

const ProductDetail: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const [product, setProduct] = useState<ProductDetailResponse | null>(null);
    const [loading, setLoading] = useState(true);
    const [reviewStats, setReviewStats] = useState<ReviewStats | null>(null);
    const [reviews, setReviews] = useState<ReviewDetail[]>([]);
    const [reviewLoading, setReviewLoading] = useState(true);
    const [thumbsSwiper, setThumbsSwiper] = useState<SwiperCore | null>(null);
    const [selectedOptions, setSelectedOptions] = useState<{ [attrId: string]: string }>({});
    const [selectedVariant, setSelectedVariant] = useState<any>(null);
    const [quantity, setQuantity] = useState(1);
    const navigate = useNavigate();
    const dispatch = useAppDispatch();

    useEffect(() => {
        if (id) {
            setLoading(true);
            getProductDetail(id)
                .then(res => setProduct(res.data.result))
                .catch(() => setProduct(null))
                .finally(() => setLoading(false));
        }
    }, [id]);

    // Gọi 2 API review riêng biệt
    useEffect(() => {
        if (id) {
            setReviewLoading(true);
            
            // Gọi API thống kê review
            const fetchStats = getProductReviewStats(id)
                .then((res: any) => {
                    console.log("Review stats:", res.data);
                    setReviewStats(res.data.result);
                })
                .catch((error: any) => {
                    console.error("Error fetching review stats:", error);
                    setReviewStats(null);
                });

            // Gọi API danh sách review
            const fetchReviews = getProductReviews(id)
                .then((res: any) => {
                    console.log("Review list:", res.data);
                    setReviews(res.data.result);
                })
                .catch((error: any) => {
                    console.error("Error fetching reviews:", error);
                    setReviews([]);
                });

            // Đợi cả 2 API hoàn thành
            Promise.all([fetchStats, fetchReviews])
                .finally(() => setReviewLoading(false));
        }
    }, [id]);

    // Tìm variant phù hợp với lựa chọn (chỉ khi đã chọn đủ thuộc tính)
    useEffect(() => {
        if (
            product &&
            product.productVariants.length > 0 &&
            product.productAttributes &&
            Object.keys(selectedOptions).length === product.productAttributes.length
        ) {
            const selectedIds = Object.values(selectedOptions);
            const found = product.productVariants.find(variant => {
                const variantIds = (variant.detailAttributes || []).map((d: any) => d.id);
                return (
                    variantIds.length === selectedIds.length &&
                    selectedIds.every(id => variantIds.includes(id))
                );
            });
            setSelectedVariant(found || null);
        } else {
            setSelectedVariant(null);
        }
    }, [selectedOptions, product]);

    // Handler khi chọn option
    const handleSelectOption = (attrId: string, valueId: string) => {
        setSelectedOptions(prev => {
            // Nếu đã chọn rồi thì bỏ chọn
            if (prev[attrId] === valueId) {
                const newOptions = { ...prev }; // tao 1 object moi
                delete newOptions[attrId]; // xoa thuoc tinh
                return newOptions; // return lai object voi cac thuoc tinh con lai
            }
            // Nếu chưa chọn thì chọn
            return { ...prev, [attrId]: valueId };
        });
    };

    // Tính toán các giá trị hợp lệ cho mỗi attribute
    const getValidValuesForAttribute = (attrId: string) => {
        if (!product) return new Set<string>();
        // Lấy các lựa chọn hiện tại, bỏ qua attrId đang xét
        const currentSelection = { ...selectedOptions };
        delete currentSelection[attrId];
        // Lọc các variant còn hàng với lựa chọn hiện tại
        const validVariants = product.productVariants.filter(variant => {
            const variantIds = (variant.detailAttributes || []).map((d: any) => d.id);
            return Object.values(currentSelection).every(id => variantIds.includes(id));
        });
        // Trả về tất cả id value của attrId này còn hợp lệ
        const validValueIds = new Set<string>();
        validVariants.forEach(variant => {
            (variant.detailAttributes || []).forEach((d: any) => {
                const attr = product.productAttributes.find(a => a.detailAttributes.some(v => v.id === d.id));
                if (attr && attr.id === attrId) {
                    validValueIds.add(d.id);
                }
            });
        });
        return validValueIds;
    };

    // Điều kiện đã chọn đủ thuộc tính
    const isAllAttributesSelected = product && product.productAttributes && Object.keys(selectedOptions).length === product.productAttributes.length;

    if (loading) return <div className="min-h-screen flex items-center justify-center">Loading...</div>;
    if (!product) return <div className="min-h-screen flex items-center justify-center text-red-500">Không tìm thấy sản phẩm</div>;

    console.log("ProductDetail render: product =", product);

    const handleBuyNow = () => {
        if (!product || !selectedVariant) return;
        dispatch(setOrderShopGroups([
            {
                shop: product.shop,
                products: [
                    {
                        id: selectedVariant.id,
                        name: product.name,
                        price: selectedVariant.price,
                        quantity,
                        image: product.images?.[0]?.url || '',
                        attrs: selectedVariant.detailAttributes?.map((a: { name: string }) => a.name) || [],
                        weight: product.weight ?? 150,
                        length: product.length ?? 0,
                        width: product.width ?? 0,
                        height: product.height ?? 0
                    }
                ]
            }
        ]));
        navigate('/checkout');
    };

    return (
        <>
            <Header />
            <section className="py-12 sm:py-16 bg-white">
                <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 lg:gap-16">
                        {/* Image Slider */}
                        <div className="slider-box w-full">
                            <Swiper
                                modules={[Thumbs]}
                                thumbs={{ swiper: thumbsSwiper && !thumbsSwiper.destroyed ? thumbsSwiper : null }}
                                className="main-slide-carousel mb-4 rounded-lg"
                            >
                                {product?.images?.map((img, index) => (
                                    <SwiperSlide key={index}>
                                        <img src={img.url} alt={`${product.name} image ${index + 1}`} className="w-full h-full object-cover rounded-lg" />
                                    </SwiperSlide>
                                ))}
                            </Swiper>
                            <Swiper
                                onSwiper={setThumbsSwiper}
                                loop={false}
                                spaceBetween={10}
                                slidesPerView={5}
                                watchSlidesProgress={true}
                                className="nav-for-slider"
                            >
                                {product?.images?.map((img, index) => (
                                    <SwiperSlide key={index} className="thumbs-slide">
                                        <img src={img.url} alt={`Thumbnail ${index + 1}`} />
                                    </SwiperSlide>
                                ))}
                            </Swiper>
                        </div>

                        {/* Product Info */}
                        <div className="pro-detail">
                            <h1 className="font-bold text-3xl leading-10 text-gray-900 mb-2">{product.name}</h1>
                            <div className="flex flex-wrap items-center gap-6 mb-4">
                                <div className="flex items-center gap-1">
                                    <StarRating rating={product.rating} />
                                </div>
                                <div className="text-gray-600 font-medium">
                                    Đã bán: <span className="font-semibold">{product.numberOfOrder}</span>
                                </div>
                                <div className="text-[#cc3333] font-extrabold text-3xl">
                                    {(selectedVariant?.price ?? 0).toLocaleString()}₫
                                </div>
                            </div>
                            <p className="text-gray-500 mb-2">{product.description || "Chưa có mô tả cho sản phẩm này."}</p>
                            <div className="mb-2">
                                                                <span className="font-semibold">Cửa hàng: </span>
                                                                <span
                                                                    className="text-[#cc3333] font-semibold cursor-pointer hover:underline"
                                                                    onClick={() => navigate(`/shop/${product.shop?.id}`)}
                                                                >
                                                                    {product.shop?.name}
                                                                </span>
                            </div>
                            {/* Hiển thị các option thuộc tính để chọn */}
                            {(!product.productAttributes || product.productAttributes.length === 0) && (
                                <div className="text-gray-500 mb-8">Sản phẩm này hiện chưa có thuộc tính để chọn.</div>
                            )}
                            {product.productAttributes && product.productAttributes.map(attr => {
                                const validValueIds = getValidValuesForAttribute(attr.id);
                                return (
                                    <div key={attr.id} className="mb-4">
                                        <div className="font-medium text-lg text-gray-900 mb-2">{attr.name}</div>
                                        <div className="flex flex-wrap gap-2">
                                            {attr.detailAttributes.map(value => {
                                                const isDisabled = validValueIds.size > 0 && !validValueIds.has(value.id);
                                                const isSelected = selectedOptions[attr.id] === value.id;
                                                return (
                                                    <button
                                                        key={value.id}
                                                        disabled={isDisabled}
                                                        className={`px-3 py-1 rounded-full border text-sm font-semibold transition-all duration-300 ${isSelected ? 'bg-indigo-600 text-white border-indigo-600' : 'bg-gray-100 text-gray-900 border-gray-200'} ${isDisabled ? 'opacity-50 cursor-not-allowed' : ''}`}
                                                        onClick={() => !isDisabled && handleSelectOption(attr.id, value.id)}
                                                    >
                                                        {value.name}
                                                    </button>
                                                );
                                            })}
                                        </div>
                                    </div>
                                );
                            })}
                            {/* Hiển thị giá và tồn kho của variant đã chọn */}
                            <div className="mb-2">
                                <span className="font-semibold">Tồn kho: </span>
                                <span>{selectedVariant?.stock ?? 0}</span>
                            </div>
                            
                           

                            {/* Quantity and Add to Cart */}
                            <div className="flex items-center flex-col min-[400px]:flex-row gap-4 mb-8">
                                <div className="flex items-center justify-center border border-gray-300 rounded-full w-full min-[400px]:w-auto">
                                    <button className="group p-3 rounded-l-full h-full flex items-center justify-center bg-white transition hover:bg-gray-50" onClick={() => setQuantity(q => Math.max(1, q - 1))}>
                                        <svg className="stroke-gray-900" width="22" height="22" viewBox="0 0 22 22" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M16.5 11H5.5" stroke="" strokeWidth="1.6" strokeLinecap="round" /></svg>
                                    </button>
                                    <input type="text" className="font-semibold text-gray-900 text-lg w-16 h-full bg-transparent text-center outline-none" value={quantity} onChange={e => setQuantity(Math.max(1, Number(e.target.value) || 1))} />
                                    <button className="group p-3 rounded-r-full h-full flex items-center justify-center bg-white transition hover:bg-gray-50" onClick={() => setQuantity(q => q + 1)}>
                                        <svg className="stroke-gray-900" width="22" height="22" viewBox="0 0 22 22" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M11 5.5V16.5M16.5 11H5.5" stroke="" strokeWidth="1.6" strokeLinecap="round" /></svg>
                                    </button>
                                </div>
                                <button 
                                    className="group py-3 px-5 rounded-full bg-[#faeaea] text-[#cc3333] font-semibold text-lg w-full flex items-center justify-center gap-2 transition-all duration-500 hover:bg-[#f5d5d5] disabled:opacity-50 disabled:cursor-not-allowed"
                                    disabled={!isAllAttributesSelected}
                                    onClick={async () => {
                                        if (!selectedVariant) return;
                                        try {
                                          await addToCart(selectedVariant.id, quantity);
                                          toast.success('Đã thêm vào giỏ hàng!');
                                        } catch (err) {
                                          toast.error('Thêm vào giỏ hàng thất bại!');
                                        }
                                      }}
                                >
                                    <svg className="stroke-[#cc3333]" width="22" height="22" viewBox="0 0 22 22" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M10.7394 17.875C10.7394 18.6344 10.1062 19.25 9.32511 19.25C8.54402 19.25 7.91083 18.6344 7.91083 17.875M16.3965 17.875C16.3965 18.6344 15.7633 19.25 14.9823 19.25C14.2012 19.25 13.568 18.6344 13.568 17.875M4.1394 5.5L5.46568 12.5908C5.73339 14.0221 5.86724 14.7377 6.37649 15.1605C6.88573 15.5833 7.61377 15.5833 9.06984 15.5833H15.2379C16.6941 15.5833 17.4222 15.5833 17.9314 15.1605C18.4407 14.7376 18.5745 14.0219 18.8421 12.5906L19.3564 9.84059C19.7324 7.82973 19.9203 6.8243 19.3705 6.16215C18.8207 5.5 17.7979 5.5 15.7522 5.5H4.1394ZM4.1394 5.5L3.66797 2.75" stroke="" strokeWidth="1.6" strokeLinecap="round" /></svg>
                                    Thêm vào giỏ hàng
                                </button>
                            </div>
                            {/* Buy Now Button */}
                            <button
                                className="text-center w-full px-5 py-4 rounded-full bg-[#cc3333] font-semibold text-lg text-white transition-all duration-500 hover:bg-[#b82d2d] disabled:opacity-50 disabled:cursor-not-allowed"
                                onClick={handleBuyNow}
                                disabled={!isAllAttributesSelected}
                            >
                                Mua ngay
                            </button>
                        </div>
                    </div>
                </div>
            </section>
            {reviewLoading ? (
                <div className="py-8 text-center">Đang tải đánh giá...</div>
            ) : reviewStats ? (
                <ReviewProduct 
                    stat={reviewStats.stat}
                    total={reviewStats.total}
                    avrRating={reviewStats.avrRating}
                    reviews={reviews}
                />
            ) : (
                <div className="py-8 text-center text-gray-500">Không thể tải đánh giá</div>
            )}
            <Footer />
            
        </>
    );
}

export default ProductDetail; 