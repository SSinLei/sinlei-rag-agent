package com.sinlei.config.config;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.*;
import java.util.Arrays;

@MappedTypes(float[].class)
@MappedJdbcTypes(JdbcType.OTHER)
public class VectorTypeHandler extends BaseTypeHandler<float[]> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, float[] parameter, JdbcType jdbcType) throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType("vector");
        StringBuilder sb = new StringBuilder("[");
        for (int j = 0; j < parameter.length; j++) {
            sb.append(parameter[j]);
            if (j < parameter.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        pgObject.setValue(sb.toString());
        ps.setObject(i, pgObject);
    }

    @Override
    public float[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseVector(rs.getString(columnName));
    }

    @Override
    public float[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseVector(rs.getString(columnIndex));
    }

    @Override
    public float[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseVector(cs.getString(columnIndex));
    }

    private float[] parseVector(String value) {
        if (value == null || value.isEmpty()) {
            return new float[0];
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("[")) {
            trimmed = trimmed.substring(1);
        }
        if (trimmed.endsWith("]")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        if (trimmed.isEmpty()) {
            return new float[0];
        }
        String[] parts = trimmed.split(",");
        float[] result = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Float.parseFloat(parts[i].trim());
        }
        return result;
    }
}
