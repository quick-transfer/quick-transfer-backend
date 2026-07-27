package com.weg.quicktransfer.repo.specifications;

import com.weg.quicktransfer.dto.place.PlaceFilter;
import com.weg.quicktransfer.enums.Park;
import com.weg.quicktransfer.enums.Section;
import com.weg.quicktransfer.exception.NullFilterException;
import com.weg.quicktransfer.model.Place;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PlaceSpecificationTest {

    @Mock
    private Root<Place> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<Object> path;

    @Mock
    private Expression<String> expression;

    @Mock
    private Predicate predicate;

    @Test
    @DisplayName("Should throw NullFilterException when filter is null")
    void shouldThrowExceptionWhenFilterIsNull() {
        assertThrows(NullFilterException.class, () -> PlaceSpecification.getFilteredPlaces(null));
    }

    @Test
    @DisplayName("Should return specification when filter is valid")
    void shouldReturnSpecificationWhenFilterIsValid() {
        PlaceFilter filter = new PlaceFilter("Jaraguá", Park.WEG_II, Section.TI);
        Specification<Place> spec = PlaceSpecification.getFilteredPlaces(filter);

        assertNotNull(spec);

        when(root.get(anyString())).thenReturn(path);
        when(criteriaBuilder.lower(any())).thenReturn(expression);
        when(criteriaBuilder.like(any(), anyString())).thenReturn(predicate);
        doReturn(predicate).when(criteriaBuilder).equal(any(), any());
        when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(predicate);

        Predicate result = spec.toPredicate(root, query, criteriaBuilder);
        assertNotNull(result);

        verify(root).get("placeName");
        verify(root).get("park");
        verify(root).get("section");
    }

    @Test
    @DisplayName("Should create specification with empty predicates when filter fields are null/blank")
    void shouldCreateSpecificationWithEmptyPredicates() {
        PlaceFilter filter = new PlaceFilter("", null, null);
        Specification<Place> spec = PlaceSpecification.getFilteredPlaces(filter);

        assertNotNull(spec);

        when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(predicate);

        Predicate result = spec.toPredicate(root, query, criteriaBuilder);
        assertNotNull(result);

        verify(root, never()).get(anyString());
    }
}
