/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.awt.Dimension
 *  java.io.InputStream
 *  java.io.PrintStream
 *  java.lang.Deprecated
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.System
 *  javax.swing.JCheckBoxMenuItem
 *  javax.swing.JFrame
 *  org.mozilla.javascript.tools.shell.Global
 */
package org.mozilla.javascript.tools.debugger;

import java.awt.Dimension;
import java.io.InputStream;
import java.io.PrintStream;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.debugger.JSInternalConsole;
import org.mozilla.javascript.tools.debugger.Menubar;
import org.mozilla.javascript.tools.debugger.ScopeProvider;
import org.mozilla.javascript.tools.debugger.SourceProvider;
import org.mozilla.javascript.tools.debugger.SwingGui;
import org.mozilla.javascript.tools.shell.Global;
import org.mozilla.javascript.tools.shell.ShellContextFactory;

public class Main {
    private SwingGui debugGui;
    private Dim dim = new Dim();

    public Main(String string2) {
        this.debugGui = new SwingGui(this.dim, string2);
    }

    public static void main(String[] arrstring) {
        Main main = new Main("Rhino JavaScript Debugger");
        main.doBreak();
        main.setExitAction(new IProxy(1));
        System.setIn((InputStream)main.getIn());
        System.setOut((PrintStream)main.getOut());
        System.setErr((PrintStream)main.getErr());
        Global global = org.mozilla.javascript.tools.shell.Main.getGlobal();
        global.setIn(main.getIn());
        global.setOut(main.getOut());
        global.setErr(main.getErr());
        main.attachTo(org.mozilla.javascript.tools.shell.Main.shellContextFactory);
        main.setScope((Scriptable)global);
        main.pack();
        main.setSize(600, 460);
        main.setVisible(true);
        org.mozilla.javascript.tools.shell.Main.exec(arrstring);
    }

    public static Main mainEmbedded(String string2) {
        ContextFactory contextFactory = ContextFactory.getGlobal();
        Global global = new Global();
        global.init(contextFactory);
        return Main.mainEmbedded(contextFactory, (Scriptable)global, string2);
    }

    public static Main mainEmbedded(ContextFactory contextFactory, Scriptable scriptable, String string2) {
        return Main.mainEmbeddedImpl(contextFactory, scriptable, string2);
    }

    public static Main mainEmbedded(ContextFactory contextFactory, ScopeProvider scopeProvider, String string2) {
        return Main.mainEmbeddedImpl(contextFactory, scopeProvider, string2);
    }

    private static Main mainEmbeddedImpl(ContextFactory contextFactory, Object object, String string2) {
        if (string2 == null) {
            string2 = "Rhino JavaScript Debugger (embedded usage)";
        }
        Main main = new Main(string2);
        main.doBreak();
        main.setExitAction(new IProxy(1));
        main.attachTo(contextFactory);
        if (object instanceof ScopeProvider) {
            main.setScopeProvider((ScopeProvider)object);
        } else {
            Scriptable scriptable = (Scriptable)object;
            if (scriptable instanceof Global) {
                Global global = (Global)scriptable;
                global.setIn(main.getIn());
                global.setOut(main.getOut());
                global.setErr(main.getErr());
            }
            main.setScope(scriptable);
        }
        main.pack();
        main.setSize(600, 460);
        main.setVisible(true);
        return main;
    }

    public void attachTo(ContextFactory contextFactory) {
        this.dim.attachTo(contextFactory);
    }

    public void clearAllBreakpoints() {
        this.dim.clearAllBreakpoints();
    }

    @Deprecated
    public void contextCreated(Context context) {
        throw new IllegalStateException();
    }

    @Deprecated
    public void contextEntered(Context context) {
        throw new IllegalStateException();
    }

    @Deprecated
    public void contextExited(Context context) {
        throw new IllegalStateException();
    }

    @Deprecated
    public void contextReleased(Context context) {
        throw new IllegalStateException();
    }

    public void detach() {
        this.dim.detach();
    }

    public void dispose() {
        this.clearAllBreakpoints();
        this.dim.go();
        this.debugGui.dispose();
        this.dim = null;
    }

    public void doBreak() {
        this.dim.setBreak();
    }

    public JFrame getDebugFrame() {
        return this.debugGui;
    }

    public PrintStream getErr() {
        return this.debugGui.getConsole().getErr();
    }

    public InputStream getIn() {
        return this.debugGui.getConsole().getIn();
    }

    public PrintStream getOut() {
        return this.debugGui.getConsole().getOut();
    }

    public void go() {
        this.dim.go();
    }

    public boolean isVisible() {
        return this.debugGui.isVisible();
    }

    public void pack() {
        this.debugGui.pack();
    }

    public void setBreakOnEnter(boolean bl) {
        this.dim.setBreakOnEnter(bl);
        this.debugGui.getMenubar().getBreakOnEnter().setSelected(bl);
    }

    public void setBreakOnExceptions(boolean bl) {
        this.dim.setBreakOnExceptions(bl);
        this.debugGui.getMenubar().getBreakOnExceptions().setSelected(bl);
    }

    public void setBreakOnReturn(boolean bl) {
        this.dim.setBreakOnReturn(bl);
        this.debugGui.getMenubar().getBreakOnReturn().setSelected(bl);
    }

    public void setExitAction(Runnable runnable) {
        this.debugGui.setExitAction(runnable);
    }

    @Deprecated
    public void setOptimizationLevel(int n) {
    }

    public void setScope(Scriptable scriptable) {
        this.setScopeProvider(IProxy.newScopeProvider(scriptable));
    }

    public void setScopeProvider(ScopeProvider scopeProvider) {
        this.dim.setScopeProvider(scopeProvider);
    }

    public void setSize(int n, int n2) {
        this.debugGui.setSize(n, n2);
    }

    @Deprecated
    public void setSize(Dimension dimension) {
        this.debugGui.setSize(dimension.width, dimension.height);
    }

    public void setSourceProvider(SourceProvider sourceProvider) {
        this.dim.setSourceProvider(sourceProvider);
    }

    public void setVisible(boolean bl) {
        this.debugGui.setVisible(bl);
    }

    private static class IProxy
    implements Runnable,
    ScopeProvider {
        public static final int EXIT_ACTION = 1;
        public static final int SCOPE_PROVIDER = 2;
        private Scriptable scope;
        private final int type;

        public IProxy(int n) {
            this.type = n;
        }

        public static ScopeProvider newScopeProvider(Scriptable scriptable) {
            IProxy iProxy = new IProxy(2);
            iProxy.scope = scriptable;
            return iProxy;
        }

        @Override
        public Scriptable getScope() {
            if (this.type != 2) {
                Kit.codeBug();
            }
            if (this.scope == null) {
                Kit.codeBug();
            }
            return this.scope;
        }

        public void run() {
            if (this.type != 1) {
                Kit.codeBug();
            }
            System.exit((int)0);
        }
    }

}

