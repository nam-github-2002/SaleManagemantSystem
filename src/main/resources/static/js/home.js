
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

    $('#sidebarCollapse').on('click', function () {
        $('#sidebar').toggleClass('active');
        $(this).toggleClass('collapsed');
        $('#mainArea').toggleClass('expanded');
        $('a.navbar-brand').toggle()
    });

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

    $('table').DataTable({
        "columnDefs": [
            { "type": "num", "targets": 3 }
        ],
        "order": [],
        "searching": false,
        "info": true,
        "paging": false
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

                $('table').DataTable({
                    "columnDefs": [
                        { "type": "num", "targets": 3 }
                    ],
                    "order": [],
                    "searching": false,
                    "info": true,
                    "paging": false
                });

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

    const submitButton = form.querySelector('button[type="submit"]');
    const hasTrashIcon = submitButton.querySelector('.fa-trash') !== null;

    if (hasTrashIcon) {
        const confirmDelete = confirm("Bạn có chắc chắn muốn xoá hàng này không?");
        if (!confirmDelete) {
            return;
        }
    }

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

// Hàm xóa ảnh
function deleteImage(event, url) {
    event.preventDefault();

    $.ajax({
        url: url,
        type: 'POST',
        success: function(response) {
            $('#mainArea').html(response);
            history.pushState(null, '', url);
        },
        error: function(xhr, status, error) {
            let errorMessage = xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message : error;
            alert(errorMessage)
        }
    });
}

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

function previewImages(event) {
    const files = event.target.files;
    const container = $('#image-preview-container');

    // Xóa ảnh cũ
    container.empty();

    if (files.length === 1) {
        // Hiển thị một ảnh với kích thước gốc
        const reader = new FileReader();
        reader.onload = function(e) {
            const img = $('<img>')
                .attr('src', e.target.result)
                .css({ width: '230px', height: '300px', objectFit: 'contain' })
                .addClass('hover-effect');
            container.append(img);
        };
        reader.readAsDataURL(files[0]);
    } else {
        Array.from(files).forEach(file => {
            const reader = new FileReader();
            reader.onload = function(e) {
                const img = $('<img>')
                    .attr('src', e.target.result)
                    .css({ width: '100px', height: '120px', objectFit: 'contain', margin: '5px' })
                    .addClass('hover-effect');
                container.append(img);
            };
            reader.readAsDataURL(file);
        });
    }
}

function changeMainImage(thumbnail) {
    let newSrc = $(thumbnail).attr('src');
    let imageId = newSrc.split('=')[1];
    $('#main-image').attr('src', '/products/display?id=' + imageId);
    $('.delete-img-btn').attr('href', '/products/image/' + imageId);
}

document.getElementById('exportExcelBtn').addEventListener('click', function() {
    exportTableToExcel('dataTable', 'DanhSachSanPham');
});

function exportTableToExcel(filename = '') {
    // Lấy bảng dữ liệu bằng jQuery
    const $table = $('table');
    if ($table.length === 0) {
        alert("Không tìm thấy bảng dữ liệu!");
        return;
    }

    // Chuyển đổi bảng thành mảng 2D
    const tableData = [];
    const $rows = $table.find('tr');

    // Duyệt qua từng hàng
    $rows.each(function() {
        const rowData = [];
        const $cells = $(this).find('td, th');

        $cells.each(function() {
            rowData.push($(this).text().trim());
        });

        tableData.push(rowData);
    });

    // Tạo một workbook mới và thêm dữ liệu vào sheet
    const wb = XLSX.utils.book_new();
    const ws = XLSX.utils.aoa_to_sheet(tableData);
    XLSX.utils.book_append_sheet(wb, ws, "Sheet1");

    // Tạo tên file
    filename = filename ? filename + '.xlsx' : 'export.xlsx';

    // Xuất file Excel
    XLSX.writeFile(wb, filename);
}
