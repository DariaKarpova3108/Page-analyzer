package gg.jte.generated.ondemand;
import hexlet.code.dto.MainPage;
public final class JteindexGenerated {
	public static final String JTE_NAME = "index.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,1,1,3,3,6,6,25,25,25,26,26,26,1,1,1,1};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, MainPage page) {
		jteOutput.writeContent("\n");
		gg.jte.generated.ondemand.layout.JtepageGenerated.render(jteOutput, jteHtmlInterceptor, page, new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\n    <div class=\"container-fluid bg-dark p-5\">\n        <div class=\"row\">\n            <div class=\"col-md-10 col-lg-8 mx-auto text-white\">\n                <h1>Анализатор страниц</h1>\n                <p>Бесплатно проверяйте сайты на SEO пригодность</p>\n                <form action=\"/\" method=\"post\">\n                    <div>\n                        <label>Ссылка</label>\n                        <input type=\"url\" name=\"url\" placeholder=\"ссылка\">\n                    </div>\n                    <div>\n                        <button>Проверить</button>\n                    </div>\n                </form>\n                <p class=\"text-secondary\">Пример: https://www.example.com</p>\n            </div>\n        </div>\n    </div>\n");
			}
		});
		jteOutput.writeContent("\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		MainPage page = (MainPage)params.get("page");
		render(jteOutput, jteHtmlInterceptor, page);
	}
}
