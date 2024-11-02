function getContent(event, url) {
    event.preventDefault();

    $.ajax({
        url: url,
        type: 'GET',
        success: function(response) {
            // Cập nhật nội dung
            $('#mainArea').html(response);

            // Cập nhật URL mà không tải lại trang
            history.pushState(null, '', url);

            // Quản lý trạng thái "active" cho các mục trong menu
            if ($(event.target).closest('.nav-item').length) {
                $('.nav-item').removeClass('active');
                $(event.target).closest('.nav-item').addClass('active');
            }
        },
        error: function(xhr, status, error) {
            let errorMessage = xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message : error;

            $('#mainArea').html(`
                <h3 class="text-center">Có lỗi xảy ra khi gửi dữ liệu</h3>
                <p class="fs-4">${errorMessage}</p>
            `);
        }
    });
}

// Xử lý khi người dùng nhấn nút Back hoặc Forward
window.onpopstate = function(event) {
    // Tải lại nội dung từ URL hiện tại
    const url = window.location.pathname;
    $.ajax({
        url: url,
        type: 'GET',
        success: function(response) {
            $('#mainArea').html(response);
        },
        error: function(xhr, status, error) {
            let errorMessage = xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message : error;
            $('#mainArea').html(`
                <h3 class="text-center">Có lỗi xảy ra khi tải nội dung</h3>
                <p class="fs-4">${errorMessage}</p>
            `);
        }
    });
};

function cancelEdit(event) {
    event.preventDefault();
    getContent(event, '/customers');
}

function postContent(event, form) {
    event.preventDefault();
    const url = form.action;
    const formData = new FormData(form); // Sử dụng FormData để gửi đúng định dạng multipart

    $.ajax({
        url: url,
        type: 'POST',
        data: formData,
        contentType: false,       // Không tự động thiết lập `Content-Type`
        processData: false,       // Không xử lý dữ liệu (FormData sẽ làm điều đó)
        success: function(response) {
            $('#mainArea').html(response);
        },
        error: function(xhr, status, error) {
            let errorMessage = xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message : error;

            $('#mainArea').html(`
                <h3 class="text-center">Có lỗi xảy ra khi gửi dữ liệu</h3>
                <p class="fs-4">${errorMessage}</p>
            `);
        }
    });
}



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
