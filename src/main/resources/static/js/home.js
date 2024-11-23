$(document).ready(function() {

    //    OPEN/CLOSE SIDEBAR
    $('#sidebarCollapse').on('click', function () {
        $('#sidebar').toggleClass('active');
        $(this).toggleClass('collapsed');
        $('#mainArea').toggleClass('expanded');
        $('a.navbar-brand').toggle()
    });

    // //Active nav-item
    // let activeMenu = localStorage.getItem('activeMenu');
    // if (activeMenu) {
    //     $('.nav-item').removeClass('active');
    //     $('a.nav-link').each(function() {
    //         if (activeMenu === $(this).attr('href')) {
    //                 $(this).closest('.nav-item').addClass('active');
    //         }
    //     });
    // } else {
    //     $('.nav-item').removeClass('active');
    //     $('a.nav-link[href="/"]').closest('.nav-item').addClass('active');
    // }

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
// Gửi request get
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

                initializeComponents();

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

// Gửi request post
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

    formData.forEach((value, key) => {
        console.log(`${key}: ${value}`);
    });


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

// Tìm kiếm
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

//Tự động focus form tìm kiếm
function focusInput() {
    $('#searchModal').on('shown.bs.modal', function () {
        $('.search-input').trigger('focus'); // This will focus the input element
    });
}

//Bật popup báo lôi
window.onload = function () {
    const errorPopup = document.getElementById('errorPopup');
    if (errorPopup) {
        errorPopup.style.display = 'block'; // Hiển thị popup
    }
};

// Đóng popup báo lôi
function closePopup() {
    const errorPopup = document.getElementById('errorPopup');
    if (errorPopup) {
        errorPopup.style.display = 'none'; // Ẩn popup
    }
}

//---------------------------------------------------ORDER----------------------------------------------------
//Cập nhật trạng thái hoá đơn
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

// Cập nhật giao diện trạng thái hoá đơn
function updateUIOrderStatus(event, selectElement) {
    const selectedOption = selectElement.options[selectElement.selectedIndex];
    selectElement.className = 'status-select ' + selectedOption.value.toLowerCase();
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
    $('#customerID').val(customer.customerID)
    $('#customerName').val(customer.name);
    $('#customerAddress').val(customer.address);
    $('#customerPhone').val(customer.phone);
    $('#customerSuggestions').empty();
}

// Hàm tìm kiếm gợi ý sản phẩm khi nhâp
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
                        $row.find(".orderDetail-productID").val(product.productID)
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

//Tạo thêm hàng cho sản phẩm trong hoá đơn
window.addProductRow = function () {
    const index = $('#productList .product-item').length; // Đếm số hàng hiện tại
    // Giả sử bạn đã có biến orderId từ phía backend (ví dụ: ${newOrder.orderId} được chuyển thành biến JS)
    const orderId = newOrderOrderId;  // Bạn có thể thay đổi từ `newOrderOrderId` thành giá trị thực tế

    const newRow = `
    <div class="row mb-2 product-item">
        <input type="hidden" name="orderDetails[${index}].order.orderId" value="${orderId}">

        <input type="hidden" class="orderDetail-productID" name="orderDetails[${index}].product.productID">

        <!-- Input tên sản phẩm -->
        <div class="col-md-4 order-product">
            <input type="text" class="form-control product-name" name="orderDetails[${index}].product.name"
                   placeholder="Nhập tên sản phẩm" oninput="searchProduct(this)">
            <div class="product-suggestions list-group"></div>
        </div>

        <!-- Input đơn giá -->
        <div class="col-md-2">
            <input type="number" class="form-control product-price" step="0.01" name="orderDetails[${index}].unitPrice"
                   placeholder="Đơn giá" min="0" value="0" oninput="updateTotal(this)">
        </div>

        <!-- Input số lượng -->
        <div class="col-md-2">
            <input type="number" class="form-control product-quantity" name="orderDetails[${index}].quantity"
                   placeholder="Số lượng" min="1" value="1" oninput="updateTotal(this)">
        </div>

        <!-- Input tổng tiền -->
        <div class="col-md-2">
            <input type="text" class="form-control product-total" name="orderDetails[${index}].totalPrice"
                   placeholder="Tổng tiền" readonly>
        </div>

        <!-- Nút xóa dòng -->
        <div class="col-md-2">
            <button type="button" class="btn btn-success"  id="addProductOnOrderBtn"  onclick="addProductRow()">+</button>
            <button type="button" class="btn btn-danger" onclick="removeProductRow(this)">-</button>
        </div>
    </div>`;

    $('#productList').append(newRow); // Thêm dòng mới vào danh sách
}

//Xoá hàng
function removeProductRow(button) {
    // Xóa dòng hiện tại
    $(button).closest('.product-item').remove();
}

//Tinh tôổng tiên một hàng
function updateTotal(input) {
    const $row = $(input).closest('.product-item');
    const price = parseFloat($row.find('.product-price').val()) || 0;
    const quantity = parseFloat($row.find('.product-quantity').val()) || 1;
    const total = price * quantity;

    $row.find('.product-total').val(total.toFixed(2)); // Cập nhật tổng tiền cho dòng
    calculateGrandTotal(); // Cập nhật tổng tiền tất cả sản phẩm
}

//Tính tổng tiền hoá đơn
function calculateGrandTotal() {
    let grandTotal = 0;
    $('.product-total').each(function () {
        grandTotal += parseFloat($(this).val()) || 0;
    });
    $('.grand-total').val(grandTotal.toFixed(2)); // Hiển thị tổng tiền tất cả sản phẩm
}

//Đóng hộp gợi ý
$(document).on("click", function (event) {
    // Kiểm tra nếu nhấp ra ngoài hộp gợi ý
    if (!$(event.target).closest("#customerSuggestions").length) {
        $("#customerSuggestions").hide(); // Ẩn hộp gợi ý
    }
});

// Hàm hiển thị hộp gợi ý (gọi khi cần, ví dụ khi nhập liệu)
function showSuggestions() {
    $("#customerSuggestions").show();
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
