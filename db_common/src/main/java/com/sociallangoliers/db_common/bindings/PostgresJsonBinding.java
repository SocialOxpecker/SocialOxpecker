package com.sociallangoliers.db_common.bindings;

import java.sql.SQLException;

import org.jooq.BindingSQLContext;
import org.jooq.impl.DSL;

import com.fasterxml.jackson.databind.JsonNode;

public class PostgresJsonBinding extends AbstractPostgresBinding {

    // Rending a bind variable for the binding context's value and casting it to the json type
    @Override
    public void sql(BindingSQLContext<JsonNode> ctx) throws SQLException {
        ctx.render().visit(DSL.val(ctx.convert(converter()).value())).sql("::json");
    }

}
