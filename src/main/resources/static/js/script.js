$(document).ready(function () {
    $('#sidebarCollapse').on('click', function () {
        $('#sidebar').toggleClass('active');
        $('.navbar').toggleClass('active'); // Thêm lớp hide-logo
    });



    const fileList = $('#file-list');

    $('#file-upload').on("change", function(event) {
        fileList.innerHTML = '';
        const files = event.target.files;
        for (let i = 0; i < files.length; i++) {
            const listItem = document.createElement("li");
            listItem.textContent = files[i].name;
            fileList.appendChild(listItem);
        }
    });
});


