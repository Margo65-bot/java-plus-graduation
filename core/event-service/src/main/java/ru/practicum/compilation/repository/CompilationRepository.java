package ru.practicum.compilation.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.QCompilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long>, QuerydslPredicateExecutor<Compilation> {

    interface Predicates {
        static Predicate buildPredicates(Boolean pinned) {
            BooleanBuilder bb = new BooleanBuilder();

            if (pinned != null) {
                bb.and(QCompilation.compilation.pinned.eq(pinned));
            }

            return bb;
        }
    }
}
