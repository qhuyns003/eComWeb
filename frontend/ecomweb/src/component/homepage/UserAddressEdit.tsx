import React, { useEffect, useState } from 'react';
import { getUserAddresses, addUserAddress, getProvinces, getDistricts, getWards } from '../../api/api';
import { FaEdit, FaTrash } from 'react-icons/fa';
// TODO: import updateUserAddress, deleteUserAddress from api if available

const UserAddressEdit: React.FC = () => {
  const [addresses, setAddresses] = useState<any[]>([]);
  const [form, setForm] = useState({
    receiverName: '',
    phoneNumber: '',
    detailAddress: '',
    ward: '',
    wardCode: '',
    district: '',
    districtId: '',
    province: '',
    provinceId: '',
    isDefault: false,
  });
  const [provinces, setProvinces] = useState<any[]>([]);
  const [districts, setDistricts] = useState<any[]>([]);
  const [wards, setWards] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [editId, setEditId] = useState<string | null>(null);
  const [deleteLoading, setDeleteLoading] = useState<string | null>(null);

  useEffect(() => {
    getUserAddresses().then(setAddresses);
  }, []);

  const handleOpenModal = () => {
    setShowModal(true);
    setEditMode(false);
    setEditId(null);
    setForm({
      receiverName: '', phoneNumber: '', detailAddress: '', ward: '', wardCode: '', district: '', districtId: '', province: '', provinceId: '', isDefault: false
    });
    setDistricts([]);
    setWards([]);
    setError('');
    setSuccess(false);
    getProvinces().then(res => setProvinces(res.data.data || []));
  };
  const handleOpenEditModal = (addr: any) => {
    setShowModal(true);
    setEditMode(true);
    setEditId(addr.id);
    setForm({ ...addr });
    setError('');
    setSuccess(false);
    getProvinces().then(res => setProvinces(res.data.data || []));
    if (addr.provinceId) {
      getDistricts(addr.provinceId).then(res => setDistricts(res.data.data || []));
    }
    if (addr.districtId) {
      getWards(addr.districtId).then(res => setWards(res.data.data || []));
    }
  };
  const handleCloseModal = () => {
    setShowModal(false);
    setError('');
    setSuccess(false);
  };
  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    if (type === 'checkbox' && e.target instanceof HTMLInputElement) {
      setForm({ ...form, [name]: e.target.checked });
    } else {
      setForm({ ...form, [name]: value });
    }
  };
  const handleProvinceChange = async (e: React.ChangeEvent<HTMLSelectElement>) => {
    const provinceId = e.target.value;
    const provinceObj = provinces.find((p: any) => String(p.ProvinceID) === provinceId);
    setForm(f => ({ ...f, province: provinceObj?.ProvinceName || '', provinceId, district: '', districtId: '', ward: '', wardCode: '' }));
    setDistricts([]);
    setWards([]);
    if (provinceId) {
      const res = await getDistricts(Number(provinceId));
      setDistricts(res.data.data || []);
    }
  };
  const handleDistrictChange = async (e: React.ChangeEvent<HTMLSelectElement>) => {
    const districtId = e.target.value;
    const districtObj = districts.find((d: any) => String(d.DistrictID) === districtId);
    setForm(f => ({ ...f, district: districtObj?.DistrictName || '', districtId, ward: '', wardCode: '' }));
    setWards([]);
    if (districtId) {
      const res = await getWards(Number(districtId));
      setWards(res.data.data || []);
    }
  };
  const handleWardChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const wardCode = e.target.value;
    const wardObj = wards.find((w: any) => String(w.WardCode) === wardCode);
    setForm(f => ({ ...f, ward: wardObj?.WardName || '', wardCode }));
  };
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess(false);
    try {
      // Build address object rõ ràng, ép kiểu boolean cho isDefault
      const addressData = {
        receiverName: form.receiverName,
        phoneNumber: form.phoneNumber,
        province: form.province,
        provinceId: form.provinceId,
        district: form.district,
        districtId: form.districtId,
        ward: form.ward,
        wardCode: form.wardCode,
        detailAddress: form.detailAddress,
        fullAddress: `${form.detailAddress}, ${form.ward}, ${form.district}, ${form.province}`,
        isDefault: !!form.isDefault,
        latitude: null,
        longitude: null
      };
      if (editMode && editId) {
        // TODO: call updateUserAddress(editId, addressData)
        // await updateUserAddress(editId, addressData);
        setSuccess(true);
      } else {
        await addUserAddress(addressData);
        setSuccess(true);
      }
      setShowModal(false);
      setForm({
        receiverName: '', phoneNumber: '', detailAddress: '', ward: '', wardCode: '', district: '', districtId: '', province: '', provinceId: '', isDefault: false
      });
      setDistricts([]);
      setWards([]);
      getUserAddresses().then(setAddresses);
    } catch (err: any) {
      setError(err?.message || 'Có lỗi xảy ra');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!window.confirm('Bạn có chắc muốn xóa địa chỉ này?')) return;
    setDeleteLoading(id);
    try {
      // TODO: call deleteUserAddress(id)
      // await deleteUserAddress(id);
      setAddresses(addresses.filter(a => a.id !== id));
    } finally {
      setDeleteLoading(null);
    }
  };

  return (
    <div className="mt-8">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-xl font-bold text-[#cc3333] tracking-tight">Địa chỉ nhận hàng</h3>
        <button onClick={handleOpenModal} className="bg-gradient-to-r from-[#cc3333] to-[#b82d2d] text-white px-5 py-2 rounded-full shadow hover:scale-105 transition-transform font-semibold flex items-center gap-2">
          <span className="text-lg">+</span> Thêm địa chỉ
        </button>
      </div>
      <div className="grid gap-4">
        {addresses.map(addr => (
          <div key={addr.id} className="rounded-xl shadow-md bg-white border border-gray-200 p-5 flex flex-col gap-2 relative group hover:shadow-lg transition">
            <div className="flex items-center justify-between mb-1">
              <div className="font-semibold text-base text-gray-800 flex items-center gap-2">
                <span className="inline-block bg-gray-100 rounded-full px-3 py-1 text-xs font-medium text-gray-600 mr-2">{addr.receiverName}</span>
                <span className="text-gray-500 text-xs">{addr.phoneNumber}</span>
                {addr.isDefault && <span className="ml-2 text-green-600 text-xs font-bold bg-green-50 px-2 py-1 rounded-full">Mặc định</span>}
              </div>
              <div className="flex gap-2 opacity-80 group-hover:opacity-100">
                <button onClick={() => handleOpenEditModal(addr)} title="Sửa" className="p-2 rounded-full hover:bg-blue-50 text-blue-600"><FaEdit /></button>
                <button onClick={() => handleDelete(addr.id)} title="Xóa" className="p-2 rounded-full hover:bg-red-50 text-red-600" disabled={deleteLoading===addr.id}>{deleteLoading===addr.id ? <span className="animate-pulse">...</span> : <FaTrash />}</button>
              </div>
            </div>
            <div className="text-gray-700 text-sm">
              <span className="font-medium">Địa chỉ:</span> {addr.fullAddress}
            </div>
            <div className="text-gray-500 text-xs">
              {addr.ward && <span>{addr.ward}, </span>}
              {addr.district && <span>{addr.district}, </span>}
              {addr.province && <span>{addr.province}</span>}
            </div>
          </div>
        ))}
        {addresses.length === 0 && <div className="text-gray-400 italic text-center">Chưa có địa chỉ nào</div>}
      </div>
      {/* Modal for add/edit address */}
      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40">
          <div className="bg-white rounded-2xl shadow-2xl p-8 w-full max-w-lg relative animate-fadeIn">
            <button onClick={handleCloseModal} className="absolute top-3 right-3 text-gray-400 hover:text-black text-2xl font-bold">&times;</button>
            <h4 className="text-xl font-bold mb-6 text-[#cc3333] tracking-tight">{editMode ? 'Sửa địa chỉ' : 'Thêm địa chỉ mới'}</h4>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <input name="receiverName" placeholder="Tên người nhận" value={form.receiverName} onChange={handleChange} className="border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#cc3333]" />
                <input name="phoneNumber" placeholder="Số điện thoại" value={form.phoneNumber} onChange={handleChange} className="border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#cc3333]" />
              </div>
              <input name="detailAddress" placeholder="Địa chỉ (số nhà, tên đường...)" value={form.detailAddress} onChange={handleChange} className="border border-gray-300 rounded-lg px-3 py-2 w-full focus:outline-none focus:ring-2 focus:ring-[#cc3333]" />
              <div className="grid grid-cols-3 gap-4">
                <select name="provinceId" value={form.provinceId || ''} onChange={handleProvinceChange} className="border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#cc3333]">
                  <option value="">Tỉnh/Thành</option>
                  {provinces.map((p: any) => (
                    <option key={p.ProvinceID} value={p.ProvinceID}>{p.ProvinceName}</option>
                  ))}
                </select>
                <select name="districtId" value={form.districtId || ''} onChange={handleDistrictChange} className="border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#cc3333]" disabled={!form.provinceId}>
                  <option value="">Quận/Huyện</option>
                  {districts.map((d: any) => (
                    <option key={d.DistrictID} value={d.DistrictID}>{d.DistrictName}</option>
                  ))}
                </select>
                <select name="wardCode" value={form.wardCode || ''} onChange={handleWardChange} className="border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#cc3333]" disabled={!form.districtId}>
                  <option value="">Phường/Xã</option>
                  {wards.map((w: any) => (
                    <option key={w.WardCode} value={w.WardCode}>{w.WardName}</option>
                  ))}
                </select>
              </div>
              <div className="flex items-center gap-2">
                <input type="checkbox" name="isDefault" checked={form.isDefault} onChange={handleChange} />
                <label className="text-sm">Đặt làm mặc định</label>
              </div>
              <div className="flex gap-3 mt-2">
                <button type="submit" className="bg-gradient-to-r from-[#cc3333] to-[#b82d2d] text-white px-6 py-2 rounded-full font-semibold hover:scale-105 transition-transform flex-1 shadow" disabled={loading}>
                  {loading ? 'Đang lưu...' : (editMode ? 'Lưu thay đổi' : 'Lưu địa chỉ')}
                </button>
                <button type="button" onClick={handleCloseModal} className="bg-gray-100 text-gray-700 px-6 py-2 rounded-full font-semibold hover:bg-gray-200 transition flex-1 shadow">Hủy</button>
              </div>
              {success && <div className="text-green-600 mt-2">{editMode ? 'Cập nhật thành công!' : 'Thêm địa chỉ thành công!'}</div>}
              {error && <div className="text-red-600 mt-2">{error}</div>}
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default UserAddressEdit;
