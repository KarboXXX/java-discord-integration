package org.karbox.dev.connection;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Tunnel;
import org.karbox.dev.DiscordIntegration;

// Ngrok is basically a way to make your server public. I'm not going to explain this bit, maybe later.
public class AutoNgrok {
    public static AutoNgrok instance;
    public NgrokClient ngrokClient;

    public Tunnel serverTunnel;
    public CreateTunnel tcpTunnel;

    public Tunnel registerTunnel() {
        this.ngrokClient = new NgrokClient.Builder().build();

        this.tcpTunnel = new CreateTunnel.Builder()
                .withName("server")
                .withProto(Proto.TCP)
                .withAddr(DiscordIntegration.getInstance().getServer().getPort())
                .build();

        this.ngrokClient.getNgrokProcess().start();

        this.serverTunnel = this.ngrokClient.connect(this.tcpTunnel);
        instance = this;

        return this.serverTunnel;
    }

    public void disconnect() throws Exception {
        if (!ngrokClient.getTunnels().isEmpty()) {
            this.ngrokClient.disconnect(this.serverTunnel.getPublicUrl());
            this.ngrokClient.getNgrokProcess().stop();
        } else {
            throw new Exception("Não é possível desconectar, não há conexões no momento... como assim?");
        }

        instance = this;
    }

    public Tunnel reconnect() throws Exception {
        if (!this.ngrokClient.getTunnels().isEmpty()) {
            throw new Exception("Não é possível reconectar," +
                    " lista de tunnel do cliente ngrok não está vazia.");
        }

        this.ngrokClient.getNgrokProcess().start();

        this.serverTunnel = this.ngrokClient.connect(this.tcpTunnel);
        instance = this;

        return this.serverTunnel;
    }

    public Tunnel reconnectWithAnotherTunnel() throws Exception {
        if (!this.ngrokClient.getTunnels().isEmpty()) {
            throw new Exception("Não é possível reconectar com outra porta," +
                    " lista de tunnel do cliente ngrok não está vazia.");
        }

        this.ngrokClient.getNgrokProcess().start();

        this.tcpTunnel = new CreateTunnel.Builder()
                .withName("server")
                .withProto(Proto.TCP)
                .withAddr(DiscordIntegration
                        .getInstance().getServer().getPort()
                ).build();

        this.serverTunnel = this.ngrokClient.connect(this.tcpTunnel);
        instance = this;

        return this.serverTunnel;
    }

    public static AutoNgrok getInstance() { return instance; }
}
