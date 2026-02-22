@import ru.mentee.power.crm.model.Lead
@import org.springframework.validation.BindingResult
@import ru.mentee.power.crm.model.LeadStatus

@param Lead lead
@param BindingResult errors = null

<form method="post">
    <div style="margin-bottom: 16px;">
        <label>Email:</label>
        <input type="email" name="email" value="${lead.getEmail()}">
        @if(errors != null && errors.hasFieldErrors("email"))
            <p style="color: red;">${errors.getFieldError("email").defaultMessage}</p>
        @endif
    </div>

    <div style="margin-bottom: 16px;">
        <label>Компания:</label>
        <input type="text" name="company" value="${lead.getCompany()}">
        @if(errors != null && errors.hasFieldErrors("company"))
            <p style="color: red;">${errors.getFieldError("company").defaultMessage}</p>
        @endif
    </div>

    <button type="submit" style="padding: 8px 16px; background: blue; color: white; border: none;">
        Сохранить
    </button>
</form>
