package com.sociallangoliers.db_common.bindings;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.util.Objects;

import org.jooq.Binding;
import org.jooq.BindingGetResultSetContext;
import org.jooq.BindingGetSQLInputContext;
import org.jooq.BindingGetStatementContext;
import org.jooq.BindingRegisterContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingSetSQLOutputContext;
import org.jooq.BindingSetStatementContext;
import org.jooq.Converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public abstract class AbstractPostgresBinding implements Binding<Object, JsonNode> {

    @Override
    public Converter<Object, JsonNode> converter() {
        return new Converter<Object, JsonNode>() {
            @Override
            public JsonNode from(Object t) {
                try {
                    return t == null ? JsonNodeFactory.instance.objectNode() : new ObjectMapper().readTree(t.toString());
                } catch (IOException e) {
                    return JsonNodeFactory.instance.objectNode();
                }
            }

            @Override
            public Object to(JsonNode u) {
                return u == null || u == JsonNodeFactory.instance.objectNode() ? null : u.toString();
            }

            @Override
            public Class<Object> fromType() {
                return Object.class;
            }

            @Override
            public Class<JsonNode> toType() {
                return JsonNode.class;
            }
        };
    }

    // Rending a bind variable for the binding context's value and casting it to which ever type
    public abstract void sql(BindingSQLContext<JsonNode> ctx) throws SQLException;

    // Registering VARCHAR types for JDBC CallableStatement OUT parameters
    @Override
    public void register(BindingRegisterContext<JsonNode> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR);
    }

    // Converting the JsonNode to a String value and setting that on a JDBC PreparedStatement
    @Override
    public void set(BindingSetStatementContext<JsonNode> ctx) throws SQLException {
        ctx.statement().setString(ctx.index(), Objects.toString(ctx.convert(converter()).value(), null));
    }

    // Getting a String value from a JDBC ResultSet and converting that to a JsonNode
    @Override
    public void get(BindingGetResultSetContext<JsonNode> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()));
    }

    // Getting a String value from a JDBC CallableStatement and converting that to a JsonNode
    @Override
    public void get(BindingGetStatementContext<JsonNode> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.statement().getString(ctx.index()));
    }

    // Setting a value on a JDBC SQLOutput (useful for Oracle OBJECT types)
    @Override
    public void set(BindingSetSQLOutputContext<JsonNode> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    // Getting a value from a JDBC SQLInput (useful for Oracle OBJECT types)
    @Override
    public void get(BindingGetSQLInputContext<JsonNode> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

}

