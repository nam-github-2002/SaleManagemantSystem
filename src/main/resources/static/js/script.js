$(document).ready(function () {
    $('#sidebarCollapse').on('click', function () {
        $('#sidebar').toggleClass('active');
        $('.navbar').toggleClass('active'); // Thêm lớp hide-logo
    });
});

// JavaScript để hiển thị thứ, ngày, và thời gian hiện tại
window.onload = function () {
    const optionsDate = {year: 'numeric', month: 'long', day: 'numeric'};
    const optionsTime = {hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false};

    const updateDateTime = () => {
        const today = new Date();
        const date = today.toLocaleDateString('vi-VN', optionsDate);
        const time = today.toLocaleTimeString('vi-VN', optionsTime);
        const dayOfWeek = today.toLocaleDateString('vi-VN', {weekday: 'long'});
        document.getElementById("current-date").innerText = `${dayOfWeek}, ${date}, ${time}`;
    };

    updateDateTime();
    setInterval(updateDateTime, 1000);
};


// =========================================
//thử xử lý thêm class active khi nhấn ở sidebar
    // Hàm để xử lý thay đổi trạng thái active khi người dùng nhấn vào menu
    function setActiveMenuItem(event) {
    // Ngăn chặn hành động mặc định nếu dùng loadContent (AJAX)
    event.preventDefault();

    // Xóa class 'active' khỏi tất cả các liên kết
    document.querySelectorAll('#sidebar .nav-link').forEach(link => {
    link.classList.remove('active');
});

    // Thêm class 'active' cho liên kết được nhấn
    event.currentTarget.classList.add('active');

    // Gọi hàm loadContent nếu cần thiết (nếu bạn muốn tải nội dung qua AJAX)
    const url = event.currentTarget.getAttribute('href');
    loadContent(event, url);
}

    // Gắn sự kiện cho tất cả các liên kết trong sidebar
    document.querySelectorAll('#sidebar .nav-link').forEach(link => {
    link.addEventListener('click', setActiveMenuItem);
});

    // Hàm loadContent để load nội dung qua AJAX nếu cần
    function loadContent(event, url) {
    // Xử lý AJAX ở đây nếu muốn cập nhật phần nội dung mà không tải lại trang
    console.log(`Đang tải nội dung từ: ${url}`);
}
// ==================================


