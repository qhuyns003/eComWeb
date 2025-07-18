import React from "react";

interface ReviewDetail {
  id: string;
  rating: number;
  comment: string;
  createdAt: string | null;
  user: {
    id: string;
    username: string;
    fullName: string;
    // ... các trường khác nếu cần
  };
  productVariant: {
    id: string;
    price: number;
    stock: number;
    detailAttributes: { id: string; name: string }[];
  };
}

interface ReviewStatProps {
  stat: { [key: string]: number };
  total: number;
  avrRating: number;
  reviews: ReviewDetail[];
}

const ReviewProduct: React.FC<ReviewStatProps> = ({ stat, total, avrRating, reviews }) => {
  // Đảm bảo các giá trị có giá trị mặc định nếu undefined/null
  const safeAvrRating = avrRating || 0;
  const safeTotal = total || 0;
  const safeStat = stat || {};
  const safeReviews = Array.isArray(reviews) ? reviews : [];
  const safeAvrRatingNumber = Number(safeAvrRating) || 0;

  return (
    <section className="bg-white py-8 antialiased md:py-16">
      <div className="mx-auto max-w-screen-xl px-4 2xl:px-0">
        <div className="flex items-center gap-2">
          <h2 className="text-2xl font-semibold text-gray-900">Đánh giá</h2>
          <div className="mt-2 flex items-center gap-2 sm:mt-0">
            <div className="flex items-center gap-0.5">
              {[1,2,3,4,5].map(star => (
                <svg key={star} className={`h-4 w-4 ${star <= Math.round(safeAvrRating) ? 'text-yellow-300' : 'text-gray-300'}`} aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M13.849 4.22c-.684-1.626-3.014-1.626-3.698 0L8.397 8.387l-4.552.361c-1.775.14-2.495 2.331-1.142 3.477l3.468 2.937-1.06 4.392c-.413 1.713 1.472 3.067 2.992 2.149L12 19.35l3.897 2.354c1.52.918 3.405-.436 2.992-2.15l-1.06-4.39 3.468-2.938c1.353-1.146.633-3.336-1.142-3.477l-4.552-.36-1.754-4.17Z" />
                </svg>
              ))}
            </div>
            <p className="text-sm font-medium leading-none text-gray-500">({safeAvrRatingNumber.toFixed(2)})</p>
            <span className="text-sm font-medium leading-none text-gray-900 underline hover:no-underline"> {safeTotal} đánh giá </span>
          </div>
        </div>
        {/* Thống kê số lượt theo từng sao */}
        <div className="my-6 gap-8 sm:flex sm:items-start md:my-8">
          <div className="shrink-0 space-y-4">
            <p className="text-2xl font-semibold leading-none text-gray-900">{safeAvrRatingNumber.toFixed(2)} trên 5</p>
            <span className="text-gray-500 text-sm">Tổng: {safeTotal} lượt đánh giá</span>
          </div>
          <div className="mt-6 min-w-0 flex-1 space-y-3 sm:mt-0">
            {[5,4,3,2,1].map(star => (
              <div key={star} className="flex items-center gap-2">
                <p className="w-2 shrink-0 text-start text-sm font-medium leading-none text-gray-900">{star}</p>
                <svg className="h-4 w-4 shrink-0 text-yellow-300" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M13.849 4.22c-.684-1.626-3.014-1.626-3.698 0L8.397 8.387l-4.552.361c-1.775.14-2.495 2.331-1.142 3.477l3.468 2.937-1.06 4.392c-.413 1.713 1.472 3.067 2.992 2.149L12 19.35l3.897 2.354c1.52.918 3.405-.436 2.992-2.15l-1.06-4.39 3.468-2.938c1.353-1.146.633-3.336-1.142-3.477l-4.552-.36-1.754-4.17Z" />
                </svg>
                <div className="h-1.5 w-80 rounded-full bg-gray-200">
                  <div className="h-1.5 rounded-full bg-yellow-300" style={{width: `${((safeStat[star] || 0) / safeTotal * 100) || 0}%`}}></div>
                </div>
                <span className="w-8 shrink-0 text-right text-sm font-medium leading-none text-indigo-700 hover:underline sm:w-auto sm:text-left">{safeStat[star] || 0} <span className="hidden sm:inline">đánh giá</span></span>
              </div>
            ))}
          </div>
        </div>
        {/* Danh sách review chi tiết */}
        <div className="mt-6 divide-y divide-gray-200">
          {safeReviews.length === 0 && <div className="text-gray-500">Chưa có đánh giá nào.</div>}
          {safeReviews.map((review) => (
            <div key={review.id} className="gap-3 pb-6 sm:flex sm:items-start">
              <div className="shrink-0 space-y-2 sm:w-48 md:w-72">
                <div className="flex items-center gap-0.5">
                  {[1,2,3,4,5].map(star => (
                    <svg key={star} className={`h-4 w-4 ${star <= review.rating ? 'text-yellow-300' : 'text-gray-300'}`} aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" viewBox="0 0 24 24">
                      <path d="M13.849 4.22c-.684-1.626-3.014-1.626-3.698 0L8.397 8.387l-4.552.361c-1.775.14-2.495 2.331-1.142 3.477l3.468 2.937-1.06 4.392c-.413 1.713 1.472 3.067 2.992 2.149L12 19.35l3.897 2.354c1.52.918 3.405-.436 2.992-2.15l-1.06-4.39 3.468-2.938c1.353-1.146.633-3.336-1.142-3.477l-4.552-.36-1.754-4.17Z" />
                    </svg>
                  ))}
                </div>
                <div className="space-y-0.5">
                  <p className="text-base font-semibold text-gray-900">{review.user?.fullName || 'Không xác định'}</p>
                  <p className="text-sm font-normal text-gray-500">{review.createdAt ? new Date(review.createdAt).toLocaleString() : ''}</p>
                </div>
              </div>
              <div className="mt-4 min-w-0 flex-1 space-y-4 sm:mt-0">
                <p className="text-base font-normal text-gray-500">{review.comment || ''}</p>
                <div className="flex items-center gap-2 flex-wrap">
                  {review.productVariant?.detailAttributes?.map(attr => (
                    <span key={attr.id} className="px-2 py-1 bg-gray-100 rounded text-xs text-gray-700 border border-gray-200 mr-2 mb-2">{attr.name}</span>
                  )) || []}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};

export default ReviewProduct; 