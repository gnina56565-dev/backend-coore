@import ru.mentee.power.crm.model.Lead
@import ru.mentee.power.crm.model.LeadStatus
@import java.util.UUID
@param Lead lead

<!DOCTYPE html>
<html>
<head>
  <title>Edit Lead</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="p-6 bg-gray-50">
  <h1 class="text-2xl font-bold mb-4 text-gray-800">Edit Lead</h1>

  <div>ID: ${lead.getId().toString()}</div>  <!-- ← UUID.toString()! -->
  <div>Email: ${lead.getEmail()}</div>

  <form method="post" action="/leads/${lead.getId().toString()}">
    <input type="hidden" name="id" value="${lead.getId().toString()}">

    <div class="mb-4">
      <label>Email:</label>
      <input type="email" name="email" value="${lead.getEmail()}" required>
    </div>

    <div class="mb-4">
      <label>Company:</label>
      <input type="text" name="company" value="${lead.getCompany()}">
    </div>

    <div class="mb-4">
      <label>Status:</label>
      <select name="status">
@if (lead.getStatus() == LeadStatus.NEW)
        <option value="NEW" selected>NEW</option>
@else
        <option value="NEW">NEW</option>
@endif
@if (lead.getStatus() == LeadStatus.CONTACTED)
        <option value="CONTACTED" selected>CONTACTED</option>
@else
        <option value="CONTACTED">CONTACTED</option>
@endif
@if (lead.getStatus() == LeadStatus.QUALIFIED)
        <option value="QUALIFIED" selected>QUALIFIED</option>
@else
        <option value="QUALIFIED">QUALIFIED</option>
@endif
      </select>
    </div>

    <button type="submit">Save</button>
    <a href="/leads">Cancel</a>
  </form>
</body>
</html>
