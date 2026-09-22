package com.wedgwoodwebworks.jinja2customdelimiters.formatting;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.wedgwoodwebworks.jinja2customdelimiters.licensing.LicenseChecker;
import com.wedgwoodwebworks.jinja2customdelimiters.licensing.LicenseGate;
import com.wedgwoodwebworks.jinja2customdelimiters.licensing.LicenseUnavailableException;
import com.wedgwoodwebworks.jinja2customdelimiters.settings.Jinja2DelimitersSettings;

public class CustomJinja2PreFormatProcessorTest extends BasePlatformTestCase {

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

    public void testProcess_convertsCustomDelimitersToStandard() {
        Jinja2DelimitersSettings settings = Jinja2DelimitersSettings.getInstance();
        settings.setBlockStartString("[%");
        settings.setBlockEndString("%]");
        settings.setVariableStartString("[[");
        settings.setVariableEndString("]]");
        settings.setCommentStartString("[#");
        settings.setCommentEndString("#]");

        String custom = "[% for item in items %]\n[[ item ]]\n[% endfor %]";
        String expected = "{% for item in items %}\n{{ item }}\n{% endfor %}";

        PsiFile file = myFixture.configureByText("test.j2", custom);
        ASTNode node = file.getNode();
        assertNotNull("AST node must not be null", node);

        CustomJinja2PreFormatProcessor processor = new CustomJinja2PreFormatProcessor();
        TextRange fileRange = file.getTextRange();
        // The processor mutates the document; wrap in a WriteCommandAction as the IDE requires
        // (runWriteAction alone triggers "Must not change document outside command" — see IDE-382054).
        com.intellij.openapi.command.WriteCommandAction.writeCommandAction(getProject()).run(() ->
            processor.process(node, fileRange)
        );

        // Commit any pending PSI changes and re-read the document text
        PsiDocumentManager.getInstance(getProject()).commitAllDocuments();
        String actual = myFixture.getEditor().getDocument().getText();
        assertEquals(
            "Custom delimiters must be converted to standard delimiters before formatting",
            expected,
            actual
        );
    }

    public void testProcess_passthrough_whenUsingStandardDelimiters() {
        // Default delimiters == standard; conversion must be a no-op.
        String standard = "{% for item in items %}\n{{ item }}\n{% endfor %}";

        PsiFile file = myFixture.configureByText("test.j2", standard);
        ASTNode node = file.getNode();
        assertNotNull(node);

        CustomJinja2PreFormatProcessor processor = new CustomJinja2PreFormatProcessor();
        com.intellij.openapi.command.WriteCommandAction.writeCommandAction(getProject()).run(() ->
            processor.process(node, file.getTextRange())
        );

        PsiDocumentManager.getInstance(getProject()).commitAllDocuments();
        assertEquals("Standard delimiters must be untouched", standard, myFixture.getEditor().getDocument().getText());
    }

    public void testProcess_throwsLicenseUnavailableException_whenNotLicensed() {
        // Stub LicenseGate to deny: ensures the hard-fail contract from Task 7
        // is enforced on the PreFormatProcessor path.
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

        PsiFile file = myFixture.configureByText("test.j2", "[% for item in items %]");
        CustomJinja2PreFormatProcessor processor = new CustomJinja2PreFormatProcessor();

        // Direct invocation: contract is that the exception escapes (must NOT be
        // swallowed by any outer try/catch). Calling through performEditorAction
        // would lose the assertion on the exception type.
        try {
            processor.process(file.getNode(), file.getTextRange());
            fail("Processor must throw LicenseUnavailableException when license check fails");
        } catch (LicenseUnavailableException expected) {
            assertNotNull(expected.getMessage());
            assertTrue(
                "Exception message must mention license verification",
                expected.getMessage().toLowerCase().contains("license")
            );
        }
    }
}