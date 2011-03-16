package org.netbeans.php.twig.parser;

import java.util.Collection;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.ParserFactory;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;
import org.netbeans.php.twig.lexer.TwigLexer;

/**
 * Netbeans parser implementation for Twig.
 *
 * @author Sebastian Hörl
 */
public class NbTwigParser extends Parser {

    List<TwigError> errors;

    TwigLexer lexer = new TwigLexer();
    TwigParser parser = new TwigParser();

    Snapshot snapshot;

    @Override
    public void parse( Snapshot snapshot, Task task, SourceModificationEvent event ) {
        errors = parser.parse( lexer.tokenize(snapshot.getText().toString(), null ) );
        this.snapshot = snapshot;
    }

    @Override
    public Result getResult(Task task) throws ParseException {
        return new NbTwigParser.Result( snapshot, errors );
    }

    @Override
    public void cancel() {}

    @Override
    public void addChangeListener( ChangeListener cl ) {}

    @Override
    public void removeChangeListener( ChangeListener cl ) {}

    public class Result extends Parser.Result {

        boolean valid = true;
        List<TwigError> errors;

        public Result( Snapshot snapshot, List<TwigError> errors ) {

            super( snapshot );
            this.errors = errors;

        }

        public List<TwigError> getErrors() throws ParseException {
            if ( !valid ) throw new ParseException();
            return errors;
        }

        @Override
        protected void invalidate() {
            valid = false;
        }

    }

    static public class Factory extends ParserFactory {

        @Override
        public Parser createParser( Collection<Snapshot> collection ) {
            return new NbTwigParser();
        }

    }

}
