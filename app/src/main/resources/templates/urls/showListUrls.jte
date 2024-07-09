@import hexlet.code.dto.UrlsPage
@import hexlet.code.util.NamedRoutes
@param UrlsPage page

<%--откорректировать ФОРМАТ ВРЕМЕНИ--%>
<%--нужно добавить лист проверок, иначе в результате добавляется только последняя проверка последнего проверенного сайта--%>

@template.layout.page(
page=page,
content = @`
    <div class="container-lg mt-5">
        <h1>Сайты</h1>
        <table class="table table-bordered table-hover mt-3">
            <thead>
            <tr>
                <th class="col-1">id</th>
                <th>Имя</th>
                <th class="col-2">Последняя проверка</th>
                <th class="col-1">Код ответа</th>
            </tr>
            </thead>
            <tbody>
            @if(!page.getUrls().isEmpty())
                @for(var url : page.getUrls())
                    <tr>
                        <td>${url.getId()}</td>
                        <td><a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()} </a></td>
                        @for(var urlCheck : page.getChecks().entrySet())
                            @if(urlCheck.getKey().getId()==url.getId())
                                <td>${urlCheck.getKey().getCreated_at().toString()}</td>
                                <td>${urlCheck.getValue().getStatus()}</td>
                            @endif
                        @endfor
                    </tr>
                @endfor
            @endif
            </tbody>
        </table>
    </div>
`
)