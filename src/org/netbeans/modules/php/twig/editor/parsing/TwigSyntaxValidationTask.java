package org.netbeans.modules.php.twig.editor.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.Parser.Result;
import org.netbeans.modules.parsing.spi.ParserResultTask;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.netbeans.modules.parsing.spi.SchedulerTask;
import org.netbeans.modules.parsing.spi.TaskFactory;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.HintsController;
import org.netbeans.spi.editor.hints.Severity;

public class TwigSyntaxValidationTask extends ParserResultTask {

    boolean cancelled = false;
    
    @Override
    public void run( Result r, SchedulerEvent se ) {
        
        TwigParserResult result = (TwigParserResult)r;
        Document document = result.getSnapshot().getSource().getDocument( false );
        
        List<ErrorDescription> errors = new ArrayList<ErrorDescription> ();
        
        for ( TwigParserResult.Error error : result.getErrors() ) {
        
            try {

                errors.add( ErrorDescriptionFactory.createErrorDescription(
                    Severity.ERROR, 
                    error.getDescription(), 
                    document, 
                    document.createPosition( error.getOffset() ), 
                    document.createPosition( error.getOffset() + error.getLength() ) 
                ) );

            } catch ( BadLocationException ex ) {}
        
        }
        
        HintsController.setErrors( document, "Twig", errors );
        
        
        cancelled = false;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public Class<? extends Scheduler> getSchedulerClass() {
        return Scheduler.EDITOR_SENSITIVE_TASK_SCHEDULER;
    }

    @Override
    public void cancel() {
        cancelled = true;
    }
    
    static public class Factory extends TaskFactory {

        @Override
        public Collection<? extends SchedulerTask> create(Snapshot snpsht) {
            return Collections.singleton( new TwigSyntaxValidationTask() );
        }
        
    }
    
}
