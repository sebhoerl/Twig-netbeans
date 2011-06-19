package org.netbeans.modules.php.twig.editor.completion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.csl.api.CodeCompletionContext;
import org.netbeans.modules.csl.api.CodeCompletionHandler;
import org.netbeans.modules.csl.api.CodeCompletionResult;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ParameterInfo;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.php.twig.editor.parsing.TwigParserResult;

public class TwigCompletionHandler implements CodeCompletionHandler {

    @Override
    public CodeCompletionResult complete( CodeCompletionContext ccc ) {        
        return CodeCompletionResult.NONE;
    }

    @Override
    public String document(ParserResult pr, ElementHandle eh) {
        return "";
    }

    @Override
    public ElementHandle resolveLink(String string, ElementHandle eh) {
        return null;
    }

    @Override
    public String getPrefix( ParserResult info, int offset, boolean upToOffset ) {
        return "";
    }

    @Override
    public QueryType getAutoQuery( JTextComponent jtc, String string ) {
        return QueryType.ALL_COMPLETION;
    }

    @Override
    public String resolveTemplateVariable(String string, ParserResult pr, int i, String string1, Map map) {
        return null;
    }

    @Override
    public Set<String> getApplicableTemplates(Document dcmnt, int i, int i1) {
        return Collections.emptySet();
    }

    @Override
    public ParameterInfo parameters(ParserResult pr, int i, CompletionProposal cp) {
        return new ParameterInfo( new ArrayList<String>(), 0, 0 );
    }
    
}
