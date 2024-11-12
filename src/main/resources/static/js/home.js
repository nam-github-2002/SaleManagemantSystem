
$(document).ready(function() {
    //Active nav-item
    let activeMenu = localStorage.getItem('activeMenu');
    if (activeMenu) {
        $('.nav-item').removeClass('active');
        $('a.nav-link').each(function() {
            if (activeMenu === $(this).attr('href')) {
                    $(this).closest('.nav-item').addClass('active');
            }
        });
    } else {
        $('.nav-item').removeClass('active');
        $('a.nav-link[href="/"]').closest('.nav-item').addClass('active');
    }

    // Sự kiện khi sử dụng nút quay lại
    $(window).on('popstate', function(event) {
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
    });

    // Hiển thị thứ, ngày, và thời gian hiện tại
    const optionsDate = { year: 'numeric', month: 'long', day: 'numeric' };
    const optionsTime = { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false };
    const updateDateTime = () => {
        const today = new Date();
        const date = today.toLocaleDateString('vi-VN', optionsDate);
        const time = today.toLocaleTimeString('vi-VN', optionsTime);
        const dayOfWeek = today.toLocaleDateString('vi-VN', { weekday: 'long' });
        $("#current-date").text(`${dayOfWeek}, ${date}, ${time}`);
    };
    updateDateTime();
    setInterval(updateDateTime, 1000);

    //Chạy hàm khi tải xong ajax
    $(document).on('ajaxComplete', function() {
        if ($.fn.DataTable) {
            $('table').DataTable({
                "columnDefs": [
                    { "type": "num", "targets": 3 }
                ],
                "order": [[3, 'asc']],
                "searching": false,
                "info": true,
                "paging": false
            });
        } else {
            console.error("DataTable chưa được import chính xác");
        }
    });

});


// Hàm get
window.getContent = function(event, url) {
    event.preventDefault();

    $.ajax({
        url: url,
        type: 'GET',
        success: function(response) {
            if ($('#mainArea').length) {

                $('#mainArea').html(response);
            } else {

                console.log('#mainArea does not exist.');
            }

            history.pushState(null, '', url);
            url = '/' + url.split("/")[3];

            $('.nav-item').removeClass('active');
            $('a.nav-link').each(function() {
                if (url === $(this).attr('href')) {
                    $(this).closest('.nav-item').addClass('active');
                }
            });
            localStorage.setItem('activeMenu', url);

        },
        error: function(xhr, status, error) {
            let errorMessage = xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message : error;
            $('#mainArea').html(`
                    <h3 class="text-center">Có lỗi xảy ra khi gửi dữ liệu</h3>
                    <p class="fs-4">${errorMessage}</p>
                `);
        }
    });
};

// Hàm post
window.postContent = function(event, form) {
    event.preventDefault();
    const url = form.action;
    const formData = new FormData(form);

    $.ajax({
        url: url,
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        success: function(response) {
            $('#mainArea').html(response);
            history.pushState(null, '', url);
        },
        error: function(xhr, status, error) {
            let errorMessage = xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message : error;
            $('#mainArea').html(`
                    <h3 class="text-center">Có lỗi xảy ra khi gửi dữ liệu</h3>
                    <p class="fs-4">${errorMessage}</p>
                `);
        }
    });
};

//Lọc sản phẩm
window.filterProductByCategory = function(event, category) {
    event.preventDefault();

    // Gửi yêu cầu AJAX đến server
    $.ajax({
        url: '/products?page=0&category=' + category,
        method: 'GET',
        success: function(response) {
            $('#mainArea').html(response);

        },
        error: function(xhr, status, error) {
            // Xử lý lỗi và thông báo người dùng
            $('#mainArea').html('<h1>Error: ' + error + '</h1>');  // Lỗi hiển thị chi tiết
        }
    });
}

//Xoá cookie khi đóng trang
window.onbeforeunload = function() {
    document.cookie = "username=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
};

function previewImage(event) {
    var reader = new FileReader();
    reader.onload = function() {
        var output = document.getElementById('image-preview');
        output.src = reader.result;
    }
    // Đọc tệp ảnh đã chọn
    reader.readAsDataURL(event.target.files[0]);
}

function changeMainImage(thumbnail) {
    // Lấy nguồn (src) của ảnh thumbnail
    let newSrc = thumbnail.src;

    // Thay đổi src của ảnh chính
    document.getElementById('main-image').src = newSrc;
}
