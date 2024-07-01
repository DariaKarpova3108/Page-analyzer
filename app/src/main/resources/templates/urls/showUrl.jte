@import hexlet.code.dto.UrlPage
@param UrlPage page

@template.layout.page(
page=page,
content = @`
    <div>
        <h1>Сайт: ${page.getUrl().getName()}</h1>
        <table>
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
        </table>
    </div>

    <div>
        <h1>Проверки</h1>
        <div>
            <button>Запустить проверку</button>
        </div>
        <div>
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Код ответа</th>
                    <th>title</th>
                    <th>h1</th>
                    <th>description</th>
                    <th>Дата проверки</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>${page.getChecks().getId()}</td>
                    <td>${page.getChecks().getStatus()}</td>
                    <td>${page.getChecks().getTitle()}</td>
                    <td>${page.getChecks().getH1()}</td>
                    <td>${page.getChecks().getDescription()}</td>
                    <td>${page.getChecks().getCreated_at().toString()}</td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
`
)