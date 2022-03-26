package com.cs.wit.acd;

import com.cs.wit.acd.basic.ACDComposeContext;
import com.cs.wit.model.AgentUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ACDComposeContextTest {

    @Test
    public void testSetAndGet(){
        ACDComposeContext ctx = new ACDComposeContext();
        ctx.setAgentUser(new AgentUser());
        ctx.getAgentUser().setOrgi("foo");
        Assertions.assertEquals(ctx.getAgentUser().getOrgi(), "foo");
    }

}