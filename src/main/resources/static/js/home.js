

$(document).ready(function() {
    let activeMenu = localStorage.getItem('activeMenu');
    if (activeMenu) {
        $(`a.nav-link[href="${activeMenu}"]`).closest('.nav-item').addClass('active');
    } else {
        $(`a.nav-link[href="/dashboard"]`).closest('.nav-item').addClass('active');
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

    // $('#sidebar .nav-item').click(function() {
    //     $('.nav-item').removeClass('active');
    //     $(this).addClass('active');
    // });
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

            $('.nav-item').removeClass('active');
            $(event.target).closest('.nav-item').addClass('active');
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
