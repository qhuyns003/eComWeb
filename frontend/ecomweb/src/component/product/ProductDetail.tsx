import React, { useState } from 'react';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Thumbs } from 'swiper/modules';
import type { Swiper as SwiperCore } from 'swiper';

// Import Swiper styles
import 'swiper/swiper-bundle.css';

// Dummy data - sau này sẽ thay bằng API
const product = {
    name: 'Túi du lịch hè màu vàng',
    category: 'HÀNH LÝ ABS',
    price: '199.00',
    discount: '30% off',
    rating: 4.8,
    colors: ['Đen', 'Nâu', 'Beige'],
    sizes: ['Nguyên bộ', '10 kg', '25 kg', '35 kg'],
    images: [
        "https://pagedone.io/asset/uploads/1700472379.png",
        "https://pagedone.io/asset/uploads/1711622397.png",
        "https://pagedone.io/asset/uploads/1711622408.png",
        "https://pagedone.io/asset/uploads/1711622419.png",
        "https://pagedone.io/asset/uploads/1711622437.png"
    ],
    colorImages: [
        "https://pagedone.io/asset/uploads/1700472379.png",
        "https://pagedone.io/asset/uploads/1700472517.png",
        "https://pagedone.io/asset/uploads/1700472529.png"
    ]
};


const ProductDetail: React.FC = () => {
    const [thumbsSwiper, setThumbsSwiper] = useState<SwiperCore | null>(null);

    return (
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
                            {product.images.map((img, index) => (
                                <SwiperSlide key={index}>
                                    <img src={img} alt={`${product.name} image ${index + 1}`} className="w-full h-full object-cover rounded-lg" />
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
                            {product.images.map((img, index) => (
                                <SwiperSlide key={index} className="thumbs-slide">
                                    <img src={img} alt={`Thumbnail ${index + 1}`} />
                                </SwiperSlide>
                            ))}
                        </Swiper>
                    </div>

                    {/* Product Info */}
                    <div className="pro-detail">
                        {/* Title and Wishlist */}
                        <div className="flex items-start justify-between gap-6 mb-6">
                            <div>
                                <h1 className="font-bold text-3xl leading-10 text-gray-900 mb-2">{product.name}</h1>
                                <p className="font-normal text-base text-gray-500">{product.category}</p>
                            </div>
                            <button className="group p-1.5 rounded-full bg-[#faeaea] hover:bg-[#f5d5d5] transition">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                    <path d="M12.843 5.51341C11.229 3.89941 8.761 3.89941 7.147 5.51341C5.533 7.12741 5.533 9.59541 7.147 11.2094L12.001 16.0634L16.855 11.2094C18.469 9.59541 18.469 7.12741 16.855 5.51341C15.241 3.89941 12.863 3.89941 12.019 5.51341" stroke="#cc3333" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
                                </svg>
                            </button>
                        </div>

                        {/* Price and Rating */}
                        <div className="flex flex-col min-[400px]:flex-row min-[400px]:items-center mb-8 gap-y-3">
                            <div className="flex items-center">
                                <h2 className="font-semibold text-2xl leading-9 text-gray-900">${product.price}</h2>
                                <span className="ml-3 font-semibold text-lg text-[#cc3333]">{product.discount}</span>
                            </div>
                            <div className="border-l border-gray-200 mx-5 h-8 max-[400px]:hidden" />
                            <div className="flex items-center gap-1">
                                <svg className="w-5 h-5 text-amber-400" fill="currentColor" viewBox="0 0 20 20"><path d="M10 15l-5.878 3.09 1.123-6.545L.489 6.91l6.572-.955L10 0l2.939 5.955 6.572.955-4.756 4.635 1.123 6.545z" /></svg>
                                <span className="text-base font-medium text-gray-600">{product.rating} (5.1k reviews)</span>
                            </div>
                        </div>

                        {/* Color Selection */}
                        <p className="font-medium text-lg text-gray-900 mb-2">Màu sắc</p>
                        <div className="grid grid-cols-3 sm:grid-cols-4 md:grid-cols-5 gap-3 mb-6 max-w-sm">
                            {product.colorImages.map((img, index) => (
                                <div key={index} className="color-box group">
                                    <img src={img} alt={`Color ${product.colors[index]}`} className="h-20 w-full object-cover border-2 border-gray-100 rounded-xl transition-all duration-500 group-hover:border-[#cc3333]" />
                                    <p className="font-normal text-sm leading-6 text-gray-500 text-center mt-2 group-hover:text-[#cc3333]">{product.colors[index]}</p>
                                </div>
                            ))}
                        </div>

                        {/* Size Selection */}
                        <p className="font-medium text-lg text-gray-900 mb-2">Kích thước</p>
                        <div className="grid grid-cols-2 min-[400px]:grid-cols-4 gap-3 mb-8">
                            {product.sizes.map(size => (
                                <button key={size} className="border border-gray-200 text-gray-900 text-sm py-2.5 rounded-full w-full font-semibold transition-all duration-300 hover:bg-gray-100 focus:bg-[#cc3333] focus:text-white">{size}</button>
                            ))}
                        </div>

                        {/* Quantity and Add to Cart */}
                        <div className="flex items-center flex-col min-[400px]:flex-row gap-4 mb-8">
                            <div className="flex items-center justify-center border border-gray-300 rounded-full w-full min-[400px]:w-auto">
                                <button className="group p-3 rounded-l-full h-full flex items-center justify-center bg-white transition hover:bg-gray-50">
                                    <svg className="stroke-gray-900" width="22" height="22" viewBox="0 0 22 22" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M16.5 11H5.5" stroke="" strokeWidth="1.6" strokeLinecap="round" /></svg>
                                </button>
                                <input type="text" className="font-semibold text-gray-900 text-lg w-16 h-full bg-transparent text-center outline-none" placeholder="1" />
                                <button className="group p-3 rounded-r-full h-full flex items-center justify-center bg-white transition hover:bg-gray-50">
                                    <svg className="stroke-gray-900" width="22" height="22" viewBox="0 0 22 22" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M11 5.5V16.5M16.5 11H5.5" stroke="" strokeWidth="1.6" strokeLinecap="round" /></svg>
                                </button>
                            </div>
                            <button className="group py-3 px-5 rounded-full bg-[#faeaea] text-[#cc3333] font-semibold text-lg w-full flex items-center justify-center gap-2 transition-all duration-500 hover:bg-[#f5d5d5]">
                                <svg className="stroke-[#cc3333]" width="22" height="22" viewBox="0 0 22 22" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M10.7394 17.875C10.7394 18.6344 10.1062 19.25 9.32511 19.25C8.54402 19.25 7.91083 18.6344 7.91083 17.875M16.3965 17.875C16.3965 18.6344 15.7633 19.25 14.9823 19.25C14.2012 19.25 13.568 18.6344 13.568 17.875M4.1394 5.5L5.46568 12.5908C5.73339 14.0221 5.86724 14.7377 6.37649 15.1605C6.88573 15.5833 7.61377 15.5833 9.06984 15.5833H15.2379C16.6941 15.5833 17.4222 15.5833 17.9314 15.1605C18.4407 14.7376 18.5745 14.0219 18.8421 12.5906L19.3564 9.84059C19.7324 7.82973 19.9203 6.8243 19.3705 6.16215C18.8207 5.5 17.7979 5.5 15.7522 5.5H4.1394ZM4.1394 5.5L3.66797 2.75" stroke="" strokeWidth="1.6" strokeLinecap="round" /></svg>
                                Thêm vào giỏ hàng
                            </button>
                        </div>
                        {/* Buy Now Button */}
                        <button className="text-center w-full px-5 py-4 rounded-full bg-[#cc3333] font-semibold text-lg text-white transition-all duration-500 hover:bg-[#b82d2d]">
                            Mua ngay
                        </button>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default ProductDetail; 