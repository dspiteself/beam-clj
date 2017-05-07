package beam_clj;

import org.apache.beam.sdk.transforms.DoFn;
import clojure.lang.IFn;
import clojure.lang.Obj;
import clojure.java.api.Clojure;
import clojure.lang.Symbol;


public class ClojureDoFn extends DoFn<Obj, Obj> {
    String ns;
    String name;
    private static IFn REQUIRE = Clojure.var("clojure.core", "require");
    private static IFn SYMBOL = Clojure.var("clojure.core", "symbol");
    private IFn f;

    public ClojureDoFn(String ns, String name) {
        this.ns = ns;
        this.name = name;
    }


    private IFn getF() {
        if (f != null) {
            return f;
        }
        REQUIRE.invoke(SYMBOL.invoke(ns));
        f = Clojure.var(this.ns, this.name);
        return f;
    }
    @ProcessElement
    public void processElement(ProcessContext c) {
        getF().invoke(c);
    }

}
