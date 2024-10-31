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

    updateDateTime();
    setInterval(updateDateTime, 1000);
};
$('#sidebar .nav-item').click(function () {
    $('.nav-item').removeClass('active');
    $(this).addClass('active');
});

function loadContent(event, url) {
    event.preventDefault();

    $.ajax({
        url: url,
        type: 'GET',
        success: function(response) {
            console.log(response)
            $('#mainArea').html(response);
            $('.nav-item').removeClass('active');
            $(event.target).closest('.nav-item').addClass('active');
        },
        error: function() {
            $('#mainArea').html('<p>Error loading content</p>');
        }
    });
}