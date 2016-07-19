package beam_clj;

import org.apache.beam.sdk.transforms.DoFn;
import clojure.lang.IFn;
import clojure.java.api.Clojure;
import clojure.lang.Symbol;


public class ClojureDoFn extends DoFn {
    String ns;
    String name;

    private IFn f;

    public ClojureDoFn(String ns, String name) {
        this.ns = ns;
        this.name = name;
    }


    private IFn getF() {
        if (f != null) {
            return f;
        }
        f = Clojure.var(this.ns, this.name);
        return f;
    }
    @Override
    public void processElement(ProcessContext c) {
        getF().invoke(c);
    }

}
