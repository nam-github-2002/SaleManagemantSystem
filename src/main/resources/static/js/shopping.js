$(document).ready(function () {
    // Cập nhật thời gian hiện tại
    const updateDateTime = () => {
        const now = new Date();
        const optionsDate = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        const optionsTime = { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false };
        const formattedDate = now.toLocaleDateString('vi-VN', optionsDate);
        const formattedTime = now.toLocaleTimeString('vi-VN', optionsTime);
        $("#current-date").text(`${formattedDate}, ${formattedTime}`);
    };
    updateDateTime();
    setInterval(updateDateTime, 1000);

    // Lắng nghe sự kiện khi nhấn vào nút "Xem chi tiết"
    $(document).on('click', '.view-details', function () {
        const name = $(this).data('name');
        const image = $(this).data('image');
        const price = $(this).data('price');

        // Cập nhật nội dung modal
        $('#productModalLabel').text(name);
        $('#modalProductName').text(name);
        $('#modalProductImage').attr('src', image);
        $('#modalProductPrice').text(price);
    });

    // Lắng nghe sự kiện click nút giảm số lượng
    $(document).on('click', '.btn-minus', function () {
        const quantityInput = $(this).closest('.product-quantity-container').find('.product-quantity');
        let quantity = parseInt(quantityInput.val());
        if (quantity > 1) {
            quantityInput.val(quantity - 1);
        }
    });

    // Lắng nghe sự kiện click nút tăng số lượng
    $(document).on('click', '.btn-plus', function () {
        const quantityInput = $(this).closest('.product-quantity-container').find('.product-quantity');
        let quantity = parseInt(quantityInput.val());
        quantityInput.val(quantity + 1);
    });
});
