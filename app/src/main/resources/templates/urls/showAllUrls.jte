@import hexlet.code.dto.UrlsPage
@import hexlet.code.util.NamedRoutes
@param UrlsPage page

@template.layout.page(
page=page,
content = @`
    <div>
        <h1>Сайты</h1>
        <table>
            <thead>
            <tr>
                <th>id</th>
                <th>Имя</th>
                <th>Последняя проверка</th>
                <th>Код ответа</th>
            </tr>
            </thead>
            <tbody>
            @if(!page.getUrls().isEmpty())
                @for(var url : page.getUrls())
                    <tr>
                        <td>${url.getId()}</td>
                        <td><a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()} </a></td>
                        <%-- дописать сюда значения в последние два столбца--%>
                        @for(var entity : page.getChecks().entrySet())
                            <td>${entity.getValue().getCreated_at().toString()}</td>
                            <td>${entity.getValue().getStatus()}</td>
                        @endfor

                    </tr>
                @endfor
            @endif
            </tbody>
        </table>
    </div>
`
)