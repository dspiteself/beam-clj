package beam_clj;

import org.apache.beam.sdk.transforms.DoFn;
import clojure.lang.IFn;


public class ClojureDoFn extends DoFn {
    IFn f;

    public ClojureDoFn(IFn f) {
        this.f = f;
    }

    @Override
    public void processElement(ProcessContext c) {
        f.invoke(c);
    }

}
