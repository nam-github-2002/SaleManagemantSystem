$(document).ready(function() {
    $(document).ready(function() {
        // Hiển thị modal nếu biến modal từ server-side là true
        let showModal = /*[[${modal}]]*/ false;
        if (showModal) {
            $('#addCustomerModal').modal('show');
        }

        // Đóng modal khi lưu khách hàng thành công
        $('#addCustomerModal').on('hidden.bs.modal', function () {
            console.log("Modal đã được đóng.");
        });

        // Hiển thị modal khi nhấn nút "Thêm Mới"
        $('#addCustomerButton').click(function() {
            $('#addCustomerModal').modal('show');
        });
    });
});