@import hexlet.code.dto.UrlPage
@import hexlet.code.util.NamedRoutes
@param UrlPage page

@template.layout.page(
page=page,
content = @`
<%--откорректировать ФОРМАТ ВРЕМЕНИ--%>

    <%--
    <div class="rounded-0 m-0 alert alert-dismissible fade show alert-danger" role="alert">
                    <p class="m-0">Некорректный URL</p>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
    --%>

<%--реализовать логику проверок так, чтобы показывались на странице конкретного сайта все проверки,
даже если они идут подряд--%>

    <div class="container-lg mt-5">
        <h1>Сайт: ${page.getUrl().getName()}</h1>
        <table class="table table-bordered table-hover mt-3">
            <tbody>
            <tr>
                <td>id</td>
                <td>${page.getUrl().getId()}</td>
            </tr>
            <tr>
                <td>Имя</td>
                <td>${page.getUrl().getName()}</td>
            </tr>
            <tr>
                <td>Дата создания</td>
                <td>${page.getUrl().getCreated_at().toString()}</td>
            </tr>
            </tbody>
        </table>
    </div>

    <div>
        <h2 class="mt-5">Проверки</h2>
        <div>
            <form method="post" name="check" action="${NamedRoutes.urlCheckPath(page.getUrl().getId())}">
                <button type="submit" class="btn btn-primary">Запустить проверку</button>
            </form>
        </div>
        <div>
            <table class="table table-bordered table-hover mt-3"> <%--понять как сузить страницу--%>
                <thead>
                <tr>
                    <th class="col-1">ID</th>
                    <th class="col-1">Код ответа</th>
                    <th>title</th>
                    <th>h1</th>
                    <th>description</th>
                    <th class="col-2">Дата проверки</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    @if(page.getChecks()!=null)
                        <td>${NamedRoutes.urlCheckPath(page.getChecks().getId())}</td>
                        <td>${page.getChecks().getStatusCode()}</td>
                        <td>${page.getChecks().getTitle()}</td>
                        <td>${page.getChecks().getH1()}</td>
                        <td>${page.getChecks().getDescription()}</td>
                        <td>${page.getChecks().getCreatedAt().toString()}</td>
                    @endif
                </tr>
                </tbody>
            </table>
        </div>
    </div>
`
)