package com.yunzhi.ssewechat.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@MappedSuperclass
@Setter
@Getter
public abstract class BaseEntity<ID extends Serializable>  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected ID id;

    private Timestamp createTime = new Timestamp(System.currentTimeMillis());

    @JsonView(DeleteAtJsonView.class)
    @Column(nullable = false)
    protected Long deleteAt = 0L;

    @JsonView(DeletedJsonView.class)
    protected Boolean deleted = false;

    @UpdateTimestamp
    protected Timestamp updateTime = new Timestamp(System.currentTimeMillis());

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity<?> that = (BaseEntity<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public interface DeletedJsonView {
    }

    public interface DeleteAtJsonView {
    }

    public interface CreateTimeJsonView {
    }
}
