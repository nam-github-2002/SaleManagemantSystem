$(document).ready(function () {
    $('#sidebarCollapse').on('click', function () {
        $('#sidebar').toggleClass('active');
        $('.navbar').toggleClass('active'); // Thêm lớp hide-logo
    });


    // product.html.js
    // Khởi tạo DataTable
    var table = $('#productTable').DataTable();
    // Lọc theo danh mục
    $('#categoryFilter').on('change', function () {
        var category = $(this).val();
        if (category) {
            // Lọc theo cột "Danh mục" (cột thứ 3, bắt đầu từ 0)
            table.column(4).search(category).draw();
        } else {
            // Hiển thị tất cả nếu chọn "Tất cả"
            table.column(4).search('').draw();
        }
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


