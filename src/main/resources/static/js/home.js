// JavaScript để hiển thị thứ, ngày, và thời gian hiện tại
window.onload = function () {
    const optionsDate = {year: 'numeric', month: 'long', day: 'numeric'};
    const optionsTime = {hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false};

    // Hàm cập nhật ngày giờ
    const updateDateTime = () => {
        const today = new Date(); // Khởi tạo mới mỗi lần cập nhật
        const date = today.toLocaleDateString('vi-VN', optionsDate);
        const time = today.toLocaleTimeString('vi-VN', optionsTime);
        const dayOfWeek = today.toLocaleDateString('vi-VN', {weekday: 'long'});
        document.getElementById("current-date").innerText = `${dayOfWeek}, ${date}, ${time}`;
    };

    updateDateTime(); // Hiển thị ngay khi tải
    setInterval(updateDateTime, 1000); // Cập nhật mỗi giây
};