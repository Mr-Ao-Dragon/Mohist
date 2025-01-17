package org.bukkit.craftbukkit.v1_16_R3.command;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.util.Waitable;
import org.bukkit.event.server.TabCompleteEvent;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
// Paper end

public class ConsoleCommandCompleter implements Completer {
    private final DedicatedServer server; // Paper - CraftServer -> DedicatedServer

    public ConsoleCommandCompleter(DedicatedServer server) { // Paper - CraftServer -> DedicatedServer
        this.server = server;
    }

    // Paper start - Change method signature for JLine update
    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        final CraftServer server = this.server.server;
        final String buffer = line.line();
        // Async Tab Complete
        com.destroystokyo.paper.event.server.AsyncTabCompleteEvent event;
        java.util.List<String> completions = new java.util.ArrayList<>();
        event = new com.destroystokyo.paper.event.server.AsyncTabCompleteEvent(server.getConsoleSender(), completions,
                buffer, true, null);
        event.callEvent();
        completions = event.isCancelled() ? com.google.common.collect.ImmutableList.of() : event.getCompletions();

        if (event.isCancelled() || event.isHandled()) {
            // Still fire sync event with the provided completions, if someone is listening
            if (!event.isCancelled() && TabCompleteEvent.getHandlerList().getRegisteredListeners().length > 0) {
                List<String> finalCompletions = completions;
                Waitable<List<String>> syncCompletions = new Waitable<List<String>>() {
                    @Override
                    protected List<String> evaluate() {
                        org.bukkit.event.server.TabCompleteEvent syncEvent = new org.bukkit.event.server.TabCompleteEvent(server.getConsoleSender(), buffer, finalCompletions);
                        return syncEvent.callEvent() ? syncEvent.getCompletions() : com.google.common.collect.ImmutableList.of();
                    }
                };
                server.getServer().processQueue.add(syncCompletions);
                try {
                    completions = syncCompletions.get();
                } catch (InterruptedException | ExecutionException e1) {
                    e1.printStackTrace();
                }
            }

            if (!completions.isEmpty()) {
                candidates.addAll(completions.stream().map(Candidate::new).collect(java.util.stream.Collectors.toList()));
            }
            return;
        }
        Waitable<List<String>> waitable = new Waitable<List<String>>() {
            @Override
            protected List<String> evaluate() {
                List<String> offers = server.getCommandMap().tabComplete(server.getConsoleSender(), buffer);

                TabCompleteEvent tabEvent = new TabCompleteEvent(server.getConsoleSender(), buffer, (offers == null) ? Collections.EMPTY_LIST : offers);
                server.getPluginManager().callEvent(tabEvent);

                return tabEvent.isCancelled() ? Collections.EMPTY_LIST : tabEvent.getCompletions();
            }
        };
        server.getServer().processQueue.add(waitable); // Paper - Remove "this."
    }
}
