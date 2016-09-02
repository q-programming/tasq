<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<!-- Image modal -->
<div class="modal fade" id="image-modal-dialog" tabindex="-1" role="dialog" aria-labelledby="Image modal">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header theme">
                <button type="button" class="close theme-close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
            </div>
            <div class="modal-body">
                <div class="row"><img class="img-responsive auto-margin" id="modal-image"></div>
                <div class="row">
                    <h4 class="text-centered">
                        <i class="fa fa-download" aria-hidden="true"></i> <span id="modal-fileLink"></span>
                    </h4>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    $(".image-modal").click(function () {
        var url = $(this).data('url');
        var filename = $(this).data('filename');
        var src = $(this).data('src');
        var downloadMsg = "<s:message code="task.downloadFile"/> ";
        var link = '<a href="' + url + '">' + downloadMsg + filename + '</a>'
        $("#modal-imageFilename").html(filename);
        $("#modal-image").attr('src', src);
        $("#modal-fileLink").html(link);
    });

</script>