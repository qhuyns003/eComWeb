import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { FiCheckCircle, FiXCircle } from 'react-icons/fi';
import { FaMoneyBillWave, FaReceipt } from 'react-icons/fa';

const PaymentSuccess: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [status, setStatus] = useState<'pending' | 'success' | 'fail'>('pending');
  const [message, setMessage] = useState('Đang xác thực thanh toán...');
  const [orderId, setOrderId] = useState<string | null>(null);
  const [amount, setAmount] = useState<string | null>(null);

  useEffect(() => {
    // Lấy tham số từ URL
    const params = new URLSearchParams(location.search);
    const vnp_TxnRef = params.get('vnp_TxnRef');
    const vnp_ResponseCode = params.get('vnp_ResponseCode');
    const vnp_TransactionStatus = params.get('vnp_TransactionStatus');
    const vnp_Amount = params.get('vnp_Amount');
    setOrderId(vnp_TxnRef);
    setAmount(vnp_Amount ? (parseInt(vnp_Amount, 10) / 100).toLocaleString() : null);

    // Gọi API backend xác thực lại trạng thái đơn hàng
    if (vnp_TxnRef) {
      axios.get(`/api/orders/payment-status?vnp_TxnRef=${vnp_TxnRef}`)
        .then(res => {
          if (res.data && res.data.code === 1000) {
            setStatus('success');
            setMessage(res.data.message || 'Thanh toán thành công!');
          } else {
            setStatus('fail');
            setMessage(res.data.message || 'Thanh toán thất bại!');
          }
        })
        .catch(() => {
          setStatus('fail');
          setMessage('Không xác thực được trạng thái thanh toán!');
        });
    } else if (vnp_ResponseCode && vnp_TransactionStatus) {
      if (vnp_ResponseCode === '00' && vnp_TransactionStatus === '00') {
        setStatus('success');
        setMessage('Thanh toán thành công!');
      } else {
        setStatus('fail');
        setMessage('Thanh toán thất bại!');
      }
    }
  }, [location.search]);

  return (
    <div className="min-h-screen flex flex-col items-center justify-center relative bg-gradient-to-br from-[#ffe5e0] via-[#fff6f0] to-[#fbeee6] overflow-hidden">
      {/* Blurred circles background */}
      <div className="absolute top-[-100px] left-[-100px] w-[300px] h-[300px] bg-[#f5d5d5] rounded-full opacity-30 blur-2xl z-0"></div>
      <div className="absolute bottom-[-120px] right-[-120px] w-[350px] h-[350px] bg-[#cc3333] rounded-full opacity-20 blur-3xl z-0"></div>
      <div className="relative z-10 bg-white rounded-3xl shadow-2xl p-10 flex flex-col items-center max-w-lg w-full border border-[#f5d5d5]">
        {status === 'pending' && (
          <div className="flex flex-col items-center gap-2">
            <div className="w-16 h-16 flex items-center justify-center mb-2 animate-spin-slow">
              <svg className="w-12 h-12 text-[#cc3333]" fill="none" viewBox="0 0 24 24"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="#cc3333" strokeWidth="4"></circle><path className="opacity-75" fill="#cc3333" d="M4 12a8 8 0 018-8v8z"></path></svg>
            </div>
            <div className="text-lg font-semibold text-gray-700">{message}</div>
          </div>
        )}
        {status === 'success' && (
          <>
            <FiCheckCircle className="text-green-500 mb-3" size={72} />
            <div className="text-3xl sm:text-4xl text-green-600 font-extrabold mb-2 tracking-tight">Thanh toán thành công!</div>
            <div className="text-lg text-gray-700 mb-2 text-center">
              Cảm ơn bạn đã mua hàng tại <span className="text-[#cc3333] font-bold">EcomWeb</span>.
            </div>
            <div className="flex items-center gap-2 text-base text-gray-600 mt-2">
              <FaReceipt className="text-[#cc3333]" />
              <span>Mã đơn hàng:</span>
              <span className="font-semibold">{orderId}</span>
            </div>
            <div className="flex items-center gap-2 text-base text-gray-600 mt-1">
              <FaMoneyBillWave className="text-green-500" />
              <span>Số tiền:</span>
              <span className="font-semibold">{amount}₫</span>
            </div>
            <div className="text-sm text-gray-400 mt-4">Bạn sẽ nhận được email xác nhận đơn hàng trong ít phút.</div>
            <button
              className="mt-8 px-10 py-3 rounded-full bg-gradient-to-r from-[#cc3333] to-[#f5d5d5] text-white font-bold text-lg shadow-lg hover:from-[#b82d2d] hover:to-[#fbeee6] transition"
              onClick={() => navigate('/')}
            >
              Về trang chủ
            </button>
          </>
        )}
        {status === 'fail' && (
          <>
            <FiXCircle className="text-red-500 mb-3" size={72} />
            <div className="text-2xl sm:text-3xl text-red-600 font-bold mb-2">Thanh toán thất bại!</div>
            <div className="text-lg text-gray-700 mb-2 text-center">{message}</div>
            <button className="mt-8 px-10 py-3 rounded-full bg-gradient-to-r from-[#cc3333] to-[#f5d5d5] text-white font-bold text-lg shadow-lg hover:from-[#b82d2d] hover:to-[#fbeee6] transition" onClick={() => navigate('/')}>Về trang chủ</button>
          </>
        )}
      </div>
    </div>
  );
};

export default PaymentSuccess; 