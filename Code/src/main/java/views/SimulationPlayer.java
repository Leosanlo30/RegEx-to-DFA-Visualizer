package views;

import controllers.SimulationTrace;
import java.util.function.Consumer;
import javax.swing.Timer;
import models.Automaton.State;

// Reproduce un recorrido paso a paso: avisa cada estado visitado y al final entrega el resultado.

final class SimulationPlayer {

    private static final int STEP_DELAY_MS = 400;

    private final Consumer<State> onStep;
    private final Consumer<SimulationTrace> onFinish;
    private final Timer timer;
    private SimulationTrace trace;
    private int step;

    SimulationPlayer(Consumer<State> onStep, Consumer<SimulationTrace> onFinish) {
        this.onStep = onStep;
        this.onFinish = onFinish;
        this.timer = new Timer(STEP_DELAY_MS, e -> advance());
    }

    void play(SimulationTrace newTrace) {
        timer.stop();
        trace = newTrace;
        step = 0;
        onStep.accept(trace.path().get(0));
        timer.start();
    }

    void stop() {
        timer.stop();
    }

    private void advance() {
        step++;
        if (step < trace.path().size()) {
            onStep.accept(trace.path().get(step));
            return;
        }
        timer.stop();
        onFinish.accept(trace);
    }
}
