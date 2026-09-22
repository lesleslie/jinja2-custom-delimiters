package com.wedgwoodwebworks.jinja2customdelimiters.formatting;

import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.wedgwoodwebworks.jinja2customdelimiters.licensing.LicenseChecker;
import com.wedgwoodwebworks.jinja2customdelimiters.licensing.LicenseGate;
import com.wedgwoodwebworks.jinja2customdelimiters.licensing.LicenseUnavailableException;
import com.wedgwoodwebworks.jinja2customdelimiters.settings.Jinja2DelimitersSettings;

public class CustomJinja2PostFormatProcessorTest extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Reset license state so tests are independent
        LicenseGate.resetChecker();
        LicenseGate.resetStateForTesting();
        // Reset settings to defaults so delimiter-conversion tests stay independent
        Jinja2DelimitersSettings settings = Jinja2DelimitersSettings.getInstance();
        if (settings != null) {
            settings.setBlockStartString("{%");
            settings.setBlockEndString("%}");
            settings.setVariableStartString("{{");
            settings.setVariableEndString("}}");
            settings.setCommentStartString("{#");
            settings.setCommentEndString("#}");
            settings.setLineStatementPrefix("");
            settings.setLineCommentPrefix("");
        }
    }

    @Override
    protected void tearDown() throws Exception {
        LicenseGate.resetChecker();
        LicenseGate.resetStateForTesting();
        super.tearDown();
    }

    public void testProcessText_restoresCustomDelimiters() {
        Jinja2DelimitersSettings settings = Jinja2DelimitersSettings.getInstance();
        settings.setBlockStartString("[%");
        settings.setBlockEndString("%]");
        settings.setVariableStartString("[[");
        settings.setVariableEndString("]]");
        settings.setCommentStartString("[#");
        settings.setCommentEndString("#]");

        String standard = "{% for item in items %}\n{{ item }}\n{% endfor %}";
        String expected = "[% for item in items %]\n[[ item ]]\n[% endfor %]";

        PsiFile file = myFixture.configureByText("test.j2", standard);

        CustomJinja2PostFormatProcessor processor = new CustomJinja2PostFormatProcessor();
        // The processor mutates the document; wrap in a WriteCommandAction as the IDE requires
        // (runWriteAction alone triggers "Must not change document outside command" — see IDE-382054).
        com.intellij.openapi.command.WriteCommandAction.writeCommandAction(getProject()).run(() ->
            processor.processText(file, file.getTextRange(), new CodeStyleSettings())
        );

        PsiDocumentManager.getInstance(getProject()).commitAllDocuments();
        String actual = myFixture.getEditor().getDocument().getText();
        assertEquals(
            "Standard delimiters must be converted back to custom delimiters after formatting",
            expected,
            actual
        );
    }

    public void testProcessText_passthrough_whenUsingCustomDelimiters() {
        // Custom delimiters == input; conversion back to custom must be a no-op.
        Jinja2DelimitersSettings settings = Jinja2DelimitersSettings.getInstance();
        settings.setBlockStartString("[%");
        settings.setBlockEndString("%]");
        settings.setVariableStartString("[[");
        settings.setVariableEndString("]]");
        settings.setCommentStartString("[#");
        settings.setCommentEndString("#]");

        String custom = "[% for item in items %]\n[[ item ]]\n[% endfor %]";

        PsiFile file = myFixture.configureByText("test.j2", custom);

        CustomJinja2PostFormatProcessor processor = new CustomJinja2PostFormatProcessor();
        com.intellij.openapi.command.WriteCommandAction.writeCommandAction(getProject()).run(() ->
            processor.processText(file, file.getTextRange(), new CodeStyleSettings())
        );

        PsiDocumentManager.getInstance(getProject()).commitAllDocuments();
        assertEquals("Custom delimiters must be untouched by post-processor", custom, myFixture.getEditor().getDocument().getText());
    }

    public void testProcessText_throwsLicenseUnavailableException_whenNotLicensed() {
        // Stub LicenseGate to deny: ensures the hard-fail contract from Task 7
        // is enforced on the PostFormatProcessor path.
        LicenseGate.setChecker(new LicenseChecker() {
            @Override
            public Boolean isLicensed() { return Boolean.FALSE; }
            @Override
            public void requestLicense(String message) { /* no-op for test */ }
        });
        LicenseGate.resetStateForTesting();

        Jinja2DelimitersSettings settings = Jinja2DelimitersSettings.getInstance();
        settings.setBlockStartString("[%");
        settings.setBlockEndString("%]");
        settings.setVariableStartString("[[");
        settings.setVariableEndString("]]");
        settings.setCommentStartString("[#");
        settings.setCommentEndString("#]");

        PsiFile file = myFixture.configureByText("test.j2", "{% for item in items %}");
        CustomJinja2PostFormatProcessor processor = new CustomJinja2PostFormatProcessor();

        // Direct invocation: contract is that the exception escapes (must NOT be
        // swallowed by any outer try/catch).
        try {
            processor.processText(file, file.getTextRange(), new CodeStyleSettings());
            fail("PostFormatProcessor must throw LicenseUnavailableException when license check fails");
        } catch (LicenseUnavailableException expected) {
            assertNotNull(expected.getMessage());
            assertTrue(
                "Exception message must mention license verification",
                expected.getMessage().toLowerCase().contains("license")
            );
        }
    }
}