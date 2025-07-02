import React from "react";

interface StarRatingProps {
  rating: number;
  max?: number;
}

const StarRating: React.FC<StarRatingProps> = ({ rating, max = 5 }) => {
  const fullStars = Math.floor(rating);
  const hasHalfStar = rating - fullStars >= 0.5;
  const emptyStars = max - fullStars - (hasHalfStar ? 1 : 0);

  return (
    <span style={{ display: "inline-flex", alignItems: "center" }}>
      {[...Array(fullStars)].map((_, i) => (
        <span key={i} style={{ color: "#FFD700", fontSize: "1.2em" }}>★</span>
      ))}
      {hasHalfStar && <span style={{ color: "#FFD700", fontSize: "1.2em" }}>☆</span>}
      {[...Array(emptyStars)].map((_, i) => (
        <span key={i + fullStars + 1} style={{ color: "#ccc", fontSize: "1.2em" }}>★</span>
      ))}
      <span style={{ marginLeft: 8, fontWeight: 500 }}>{rating.toFixed(1)}</span>
    </span>
  );
};

export default StarRating; 