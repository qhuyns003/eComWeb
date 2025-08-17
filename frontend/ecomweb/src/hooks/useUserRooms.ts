import { useEffect, useState } from "react";
import { fetchUserRooms, fetchRoomById } from "../api/api";
// tach hooks de code trong 1 component bot dai
export function useUserRooms() {
  const [rooms, setRooms] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    setError(null);
    fetchUserRooms()
      .then(async (res) => {
        const result = res.data?.result;
        if (!result) throw new Error("No rooms found");
        // Lấy chi tiết từng phòng
        const roomDetails = await Promise.all(
          result.map(async (item: any) => {
            const roomId = item.key.roomId;
            const detail = await fetchRoomById(roomId);
            return { ...item, room: detail.data?.result };
          })
        );
        setRooms(roomDetails);
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  return { rooms, loading, error };
}
