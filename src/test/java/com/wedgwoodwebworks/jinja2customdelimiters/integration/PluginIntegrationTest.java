package com.wedgwoodwebworks.jinja2delimiters.integration;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.wedgwoodwebworks.jinja2delimiters.lang.CustomJinja2FileType;
import com.wedgwoodwebworks.jinja2delimiters.settings.Jinja2DelimitersSettings;

/**
 * Integration tests to verify the plugin works end-to-end
 */
public class PluginIntegrationTest extends BasePlatformTestCase {

    private Jinja2DelimitersSettings settings;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        settings = Jinja2DelimitersSettings.getInstance();
        // Reset to default settings
        settings.blockStartString = "{%";
        settings.blockEndString = "%}";
        settings.variableStartString = "{{";
        settings.variableEndString = "}}";
        settings.commentStartString = "{#";
        settings.commentEndString = "#}";
        settings.lineStatementPrefix = "";
        settings.lineCommentPrefix = "";
    }

    public void testFileTypeRegistration() {
        FileTypeManager fileTypeManager = FileTypeManager.getInstance();
        
        // Test standard Jinja2 extensions
        FileType j2Type = fileTypeManager.getFileTypeByExtension("j2");
        assertTrue("j2 files should be recognized", 
                   j2Type instanceof CustomJinja2FileType || j2Type.getName().contains("Jinja2"));
        
        FileType jinja2Type = fileTypeManager.getFileTypeByExtension("jinja2");
        assertTrue("jinja2 files should be recognized", 
                   jinja2Type instanceof CustomJinja2FileType || jinja2Type.getName().contains("Jinja2"));
        
        FileType htmlJ2Type = fileTypeManager.getFileTypeByExtension("html.j2");
        assertTrue("html.j2 files should be recognized", 
                   htmlJ2Type instanceof CustomJinja2FileType || htmlJ2Type.getName().contains("Jinja2"));
    }

    public void testBasicTemplateParsing() {
        String templateContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>{{ title }}</title>
            </head>
            <body>
                <h1>{{ heading }}</h1>
                {% for item in items %}
                    <p>{{ item.name }}</p>
                {% endfor %}
                
                {% if user %}
                    <p>Welcome, {{ user.name }}!</p>
                {% else %}
                    <p>Please log in.</p>
                {% endif %}
            </body>
            </html>
            """;

        PsiFile psiFile = myFixture.configureByText("test.j2", templateContent);
        assertNotNull("PsiFile should be created", psiFile);
        
        // Verify the file is recognized as our custom type
        assertTrue("File should be recognized as Jinja2 template",
                   psiFile.getFileType() instanceof CustomJinja2FileType ||
                   psiFile.getFileType().getName().contains("Jinja2"));
    }

    public void testCustomDelimitersParsing() {
        // Configure custom delimiters
        settings.blockStartString = "<%";
        settings.blockEndString = "%>";
        settings.variableStartString = "[[";
        settings.variableEndString = "]]";
        settings.commentStartString = "<#";
        settings.commentEndString = "#>";

        String templateContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>[[ title ]]</title>
            </head>
            <body>
                <# This is a comment #>
                <h1>[[ heading ]]</h1>
                <% for item in items %>
                    <p>[[ item.name ]]</p>
                <% endfor %>
                
                <% if user %>
                    <p>Welcome, [[ user.name ]]!</p>
                <% else %>
                    <p>Please log in.</p>
                <% endif %>
            </body>
            </html>
            """;

        PsiFile psiFile = myFixture.configureByText("test_custom.j2", templateContent);
        assertNotNull("PsiFile should be created with custom delimiters", psiFile);
        
        // Verify the file is recognized as our custom type
        assertTrue("File should be recognized as custom Jinja2 template",
                   psiFile.getFileType() instanceof CustomJinja2FileType ||
                   psiFile.getFileType().getName().contains("Jinja2"));
    }

    public void testLineBasedSyntax() {
        // Configure line-based syntax
        settings.lineStatementPrefix = "%";
        settings.lineCommentPrefix = "##";

        String templateContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>{{ title }}</title>
            </head>
            <body>
                ## This is a line comment
                <h1>{{ heading }}</h1>
                % for item in items
                    <p>{{ item.name }}</p>
                % endfor
                
                % if user
                    <p>Welcome, {{ user.name }}!</p>
                % else
                    <p>Please log in.</p>
                % endif
            </body>
            </html>
            """;

        PsiFile psiFile = myFixture.configureByText("test_line.j2", templateContent);
        assertNotNull("PsiFile should be created with line-based syntax", psiFile);
    }

    public void testComplexTemplate() {
        String templateContent = """
            {% extends "base.html" %}
            {% import "macros.html" as macros %}
            {% from "utils.html" import format_date, format_price %}
            
            {% block title %}{{ product.name }} - Shop{% endblock %}
            
            {% block content %}
                <div class="product-detail">
                    <h1>{{ product.name | title }}</h1>
                    
                    {% if product.images %}
                        <div class="images">
                            {% for image in product.images %}
                                <img src="{{ image.url }}" alt="{{ image.alt_text or product.name }}">
                            {% endfor %}
                        </div>
                    {% endif %}
                    
                    <div class="product-info">
                        <p class="price">{{ format_price(product.price) }}</p>
                        
                        {% if product.discount %}
                            <p class="discount">
                                Save {{ product.discount.percentage }}%!
                                <span class="original-price">{{ format_price(product.original_price) }}</span>
                            </p>
                        {% endif %}
                        
                        <div class="description">
                            {{ product.description | safe }}
                        </div>
                        
                        {% set reviews_count = product.reviews | length %}
                        {% if reviews_count > 0 %}
                            <div class="reviews-summary">
                                <h3>Customer Reviews ({{ reviews_count }})</h3>
                                <div class="rating">
                                    {{ macros.render_stars(product.average_rating) }}
                                    <span>{{ product.average_rating | round(1) }}/5</span>
                                </div>
                            </div>
                            
                            {% for review in product.reviews[:3] %}
                                <div class="review">
                                    <h4>{{ review.title }}</h4>
                                    <p>{{ review.content | truncate(200) }}</p>
                                    <small>
                                        By {{ review.author }} on {{ format_date(review.date) }}
                                        {{ macros.render_stars(review.rating) }}
                                    </small>
                                </div>
                            {% endfor %}
                        {% endif %}
                    </div>
                    
                    <form method="POST" action="{{ url_for('add_to_cart', product_id=product.id) }}">
                        {{ csrf_token() }}
                        
                        {% if product.variants %}
                            {% for variant_type, variants in product.variants.items() %}
                                <div class="variant-group">
                                    <label>{{ variant_type | title }}:</label>
                                    <select name="{{ variant_type }}" required>
                                        {% for variant in variants %}
                                            <option value="{{ variant.id }}" 
                                                    {% if variant.stock == 0 %}disabled{% endif %}>
                                                {{ variant.name }}
                                                {% if variant.stock == 0 %} (Out of Stock){% endif %}
                                            </option>
                                        {% endfor %}
                                    </select>
                                </div>
                            {% endfor %}
                        {% endif %}
                        
                        <div class="quantity">
                            <label for="quantity">Quantity:</label>
                            <input type="number" name="quantity" id="quantity" 
                                   min="1" max="{{ product.max_quantity }}" value="1" required>
                        </div>
                        
                        <button type="submit" class="btn-primary" 
                                {% if not product.in_stock %}disabled{% endif %}>
                            {% if product.in_stock %}
                                Add to Cart
                            {% else %}
                                Out of Stock
                            {% endif %}
                        </button>
                    </form>
                </div>
                
                {% call macros.render_section("related-products") %}
                    <h3>You might also like</h3>
                    {% for related in product.related_products %}
                        {{ macros.product_card(related) }}
                    {% endfor %}
                {% endcall %}
            {% endblock %}
            
            {% block scripts %}
                {{ super() }}
                <script src="{{ url_for('static', filename='js/product-detail.js') }}"></script>
                
                {% if config.ANALYTICS_ENABLED %}
                    <script>
                        analytics.track('Product Viewed', {
                            product_id: {{ product.id | tojson }},
                            product_name: {{ product.name | tojson }},
                            category: {{ product.category.name | tojson }},
                            price: {{ product.price }}
                        });
                    </script>
                {% endif %}
            {% endblock %}
            """;

        PsiFile psiFile = myFixture.configureByText("complex_template.j2", templateContent);
        assertNotNull("Complex PsiFile should be created", psiFile);
        
        // Verify parsing didn't fail
        assertTrue("File should be recognized as Jinja2 template",
                   psiFile.getFileType() instanceof CustomJinja2FileType ||
                   psiFile.getFileType().getName().contains("Jinja2"));
    }

    public void testMixedDelimitersConfiguration() {
        // Test a configuration that mixes custom and default delimiters
        settings.blockStartString = "<%";
        settings.blockEndString = "%>";
        // Keep variable delimiters as default
        settings.variableStartString = "{{";
        settings.variableEndString = "}}";
        settings.commentStartString = "<#";
        settings.commentEndString = "#>";

        String templateContent = """
            <# Mixed delimiter template #>
            <html>
            <body>
                <h1>{{ title }}</h1>
                <% for item in items %>
                    <p>{{ item.name }}</p>
                <% endfor %>
            </body>
            </html>
            """;

        PsiFile psiFile = myFixture.configureByText("mixed_delims.j2", templateContent);
        assertNotNull("PsiFile should be created with mixed delimiters", psiFile);
    }

    public void testErrorRecovery() {
        // Test that malformed templates don't crash the parser
        String malformedTemplate = """
            {{ unclosed_variable
            {% unclosed_block
            {# unclosed comment
            <p>Some HTML content</p>
            {{ valid_variable }}
            {% valid_block %}content{% endblock %}
            """;

        PsiFile psiFile = myFixture.configureByText("malformed.j2", malformedTemplate);
        assertNotNull("PsiFile should be created even with malformed content", psiFile);
    }

    @Override
    protected void tearDown() throws Exception {
        // Reset settings after each test
        if (settings != null) {
            settings.blockStartString = "{%";
            settings.blockEndString = "%}";
            settings.variableStartString = "{{";
            settings.variableEndString = "}}";
            settings.commentStartString = "{#";
            settings.commentEndString = "#}";
            settings.lineStatementPrefix = "";
            settings.lineCommentPrefix = "";
        }
        super.tearDown();
    }
}