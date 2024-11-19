$(document).ready(function() {

    //    OPEN/CLOSE SIDEBAR
    $('#sidebarCollapse').on('click', function () {
        $('#sidebar').toggleClass('active');
        $(this).toggleClass('collapsed');
        $('#mainArea').toggleClass('expanded');
        $('a.navbar-brand').toggle()
    });

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

    //TẠO DATA TABLE
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

//---------------------------------------------------GENERAL----------------------------------------------------
// Hàm get
window.getContent = function(event, url) {
    event.preventDefault();

    $.ajax({
        url: url,
        type: 'GET',
        success: function(response) {
            if (response.includes('login.html') || response.includes('form-login')) {
                // Render trực tiếp vào document
                document.documentElement.innerHTML = response;
                return;
            }

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
    $('#searchModal').modal('hide');

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
            if (response.includes('login.html') || response.includes('form-login')) {
                // Render trực tiếp vào document
                document.documentElement.innerHTML = response;
                return;
            }

            $('#mainArea').html(response);
            history.pushState(null, '', url);
            $('#searchModal').modal('hide');
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

// search
window.searchContent = function(event, form) {
    event.preventDefault();
    $('#searchModal').modal('hide');

    const url = form.action;
    const keyword = form.querySelector('input[name="keyword"]').value;
    const page = 0; // Mặc định tìm kiếm ở trang đầu tiên

    // Tạo URL với tham số tìm kiếm
    const queryUrl = `${url}?keyword=${encodeURIComponent(keyword)}&page=${page}`;

    $.ajax({
        url: queryUrl,
        type: 'GET',
        success: function(response) {
            // Nếu trả về login page
            if (response.includes('login.html') || response.includes('form-login')) {
                document.documentElement.innerHTML = response;
                return;
            }

            // Render kết quả tìm kiếm
            $('#mainArea').html(response);
            history.pushState(null, '', queryUrl); // Cập nhật URL trên trình duyệt
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



//Xoá cookie khi đóng trang
window.onbeforeunload = function() {
    document.cookie = "username=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
};

function closeModal() {
    $('#searchModal').modal('hide');
}
function handleEnter(event) {
    if (event.key === 'Enter') {
        postContent(event);  // Gọi hàm postContent nếu bạn muốn gửi form ngay lập tức
        closeModal();         // Đóng modal sau khi gửi form
    }
}

function focusInput() {
    $('#searchModal').on('shown.bs.modal', function () {
        $('.search-input').trigger('focus'); // This will focus the input element
    });
}

function updateUIOrderStatus(event, selectElement) {
    const selectedOption = selectElement.options[selectElement.selectedIndex];
    selectElement.className = 'status-select ' + selectedOption.value.toLowerCase();
}


//---------------------------------------------------ORDER----------------------------------------------------
//Cập nhật trạng thái hoa đơn
function updateOrderStatus(event, selectElement) {
    event.preventDefault();

    // Lấy URL từ data-target
    var url = selectElement.dataset.target;

    // Lấy trạng thái đơn hàng mới từ select
    var orderStatus = selectElement.value;

    // Gửi yêu cầu POST sử dụng jQuery AJAX
    $.ajax({
        url: url,  // Sử dụng URL đã lấy từ data-target
        method: 'POST',  // Phương thức HTTP là POST
        contentType: 'application/json',  // Định dạng gửi đi là JSON
        data: JSON.stringify({
            orderStatus: orderStatus  // Trạng thái mới của đơn hàng
        }),
        success: function(response) {
            // Xử lý phản hồi thành công
            if (response.success) {
                alert('Order status updated successfully!');
            } else {
                alert('Failed to update order status.');
            }
        },
        error: function(xhr, status, error) {
            // Xử lý lỗi khi gửi yêu cầu
            console.error('Error:', error);
            alert('An error occurred while updating the order status.');
        }
    });
}


// Hàm tìm kiếm khách hàng
function searchCustomer(input) {
    let query = $(input).val().trim();
    const $suggestionBox = $('#customerSuggestions');

    if (query.length < 2) {
        $suggestionBox.slideUp(200); // Thêm animation ẩn
        $suggestionBox.empty();
        return;
    }


    // Gọi Ajax để tìm kiếm khách hàng
    $.ajax({
        url: `/customers/searchProductName`,
        method: 'GET',
        data: { query: query },
        dataType: 'json',
        success: function (customers) {
            if (customers.length === 0) {
                $suggestionBox.slideUp(200);
                return;
            }
            $suggestionBox.empty();

            customers.forEach(customer => {
                const $item = $('<a>')
                    .addClass('list-group-item list-group-item-action')
                    .text(customer.name)
                    .on('click', function () {
                        selectCustomer(customer);
                    });
                $suggestionBox.append($item);
            });

            // Hiển thị box gợi ý với hiệu ứng slideDown
            if (!$suggestionBox.is(':visible')) {
                $suggestionBox.slideDown(200); // Hiển thị với hiệu ứng
            }
        },
        error: function () {
            $suggestionBox.slideUp(200);
        }

    });
}

// Hàm điền thông tin khách hàng khi chọn
function selectCustomer(customer) {
    $('#customerName').val(customer.name);
    $('#customerAddress').val(customer.address);
    $('#customerPhone').val(customer.phone);
    $('#customerSuggestions').empty();
}

// Hàm tìm kiếm sản phẩm
function searchProduct(input) {
    let query = $(input).val().trim(); // Lấy giá trị input và loại bỏ khoảng trắng
    let $suggestionBox = $(input).siblings('.product-suggestions'); // Tìm sibling là product-suggestions

    // Khi độ dài ký tự nhỏ hơn 2, ẩn box gợi ý
    if (query.length < 2) {
        $suggestionBox.slideUp(200);
        return;
    }

    $.ajax({
        url: `/products/searchProductName`,
        method: 'GET',
        data: { query: query },
        dataType: 'json',
        success: function (products) {
            if (products.length === 0) {
                $suggestionBox.slideUp(200);
                return;
            }
            $suggestionBox.empty();
            products.forEach(product => {
                const $item = $('<a>')
                    .addClass('list-group-item list-group-item-action')
                    .text(product.productName)
                    .on('click', function () {
                        const $row = $(input).closest('.row'); // Tìm dòng cha

                        // Gán giá trị vào các ô liên quan
                        $(input).val(product.productName);
                        $row.find(".product-price").val(product.price); // Điền đơn giá
                        updateTotal($row.find(".product-quantity")); // Cập nhật tổng tiền của dòng hiện tại
                        calculateGrandTotal(); // Tính tổng tiền toàn bộ

                        // Xóa danh sách gợi ý
                        $suggestionBox.empty();
                    });
                $suggestionBox.append($item);
            });

            // Hiển thị box gợi ý với hiệu ứng slideDown
            if (!$suggestionBox.is(':visible')) {
                $suggestionBox.slideDown(200);
            }
        },
        error: function () {
            $suggestionBox.slideUp(200);
        }
    });
}

function addProductRow() {
    const newRow = `
    <div class="row mb-2 product-item">
        <div class="col-md-4 order-product">
            <input type="text" class="form-control product-name" name="query"
                   placeholder="Nhập tên sản phẩm" oninput="searchProduct(this)">
            <div class="product-suggestions list-group"></div>
        </div>
        <div class="col-md-2">
            <input type="number" class="form-control product-price" placeholder="Đơn giá" min="0" value="0" oninput="updateTotal(this)">
        </div>
        <div class="col-md-2">
            <input type="number" class="form-control product-quantity" placeholder="Số lượng" min="1" value="1" oninput="updateTotal(this)">
        </div>
        <div class="col-md-2">
            <input type="text" class="form-control product-total" placeholder="Tổng tiền" readonly>
        </div>
        <div class="col-md-2">
            <button type="button" class="btn btn-success" onclick="addProductRow()">+</button>
            <button type="button" class="btn btn-danger" onclick="removeProductRow(this)">-</button>
        </div>
    </div>
    `;
    $('#productList').append(newRow);
}

function removeProductRow(button) {
    $(button).closest('.product-item').remove();
    calculateGrandTotal();
}

function updateTotal(input) {
    const $row = $(input).closest('.product-item');
    const price = parseFloat($row.find('.product-price').val()) || 0;
    const quantity = parseFloat($row.find('.product-quantity').val()) || 1;
    const total = price * quantity;

    $row.find('.product-total').val(total.toFixed(2)); // Cập nhật tổng tiền cho dòng
    calculateGrandTotal(); // Cập nhật tổng tiền tất cả sản phẩm
}

function calculateGrandTotal() {
    let grandTotal = 0;
    $('.product-total').each(function () {
        grandTotal += parseFloat($(this).val()) || 0;
    });
    $('.grand-total').val(grandTotal.toFixed(2)); // Hiển thị tổng tiền tất cả sản phẩm
}
// Hàm xóa hàng hóa
function removeProductRow(button) {
    $(button).closest('.product-item').remove();
}


//------------------------------------------------------PRODUCT-------------------------------------------------

//Hiển thị ảnh khi tải lên
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

//Xem ảnh chi tiết trong danh sách ảnh
function changeMainImage(thumbnail) {
    let newSrc = $(thumbnail).attr('src');
    let imageId = newSrc.split('=')[1];
    $('#main-image').attr('src', '/products/display?id=' + imageId);
    $('.delete-img-btn').attr('href', '/products/image/' + imageId);
}

//Xuất dữ liệu ra file excel
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
function filterProductByCategory(event, category) {
    event.preventDefault();


    // Gửi yêu cầu AJAX đến server
    $.ajax({
        url: '/products', // Gọi URL gốc của products
        method: 'GET',
        data: {
            page: 0, // Đặt lại trang về 0 khi lọc
            category: category,
            keyword: $('#search-input').val() // Lấy giá trị từ input search (nếu có)
        },
        success: function(response) {
            $('#mainArea').html(response);

            // Tạo URL mới cho history.pushState
            const newUrl = '/products?page=0&category=' + category + '&keyword=' + $('#search-input').val();
            history.pushState({category: category}, '', newUrl);
        },
        error: function(xhr, status, error) {
            $('#mainArea').html('<h1>Error: ' + error + '</h1>'); // Xử lý lỗi
        }
    });
}

//------------------------------------------------------Customer-------------------------------------------------

