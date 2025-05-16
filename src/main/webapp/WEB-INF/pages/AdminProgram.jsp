<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Program Management - Moove</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/Header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/Footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/AdminProgram.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <style>
        .modal {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            z-index: 1000;
            align-items: center;
            justify-content: center;
        }
        .modal-content {
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            width: 400px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            position: relative;
        }
        .close {
            position: absolute;
            right: 10px;
            top: 10px;
            font-size: 24px;
            cursor: pointer;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
        }
        .form-group input {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .form-buttons {
            text-align: right;
        }
        .btn {
            padding: 8px 16px;
            margin-left: 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .btn-submit { background-color: #4CAF50; color: white; }
        .btn-delete { background-color: #f44336; color: white; }
        .btn-cancel { background-color: #ddd; }
        .btn:hover { opacity: 0.9; }
    </style>
</head>
<body>
<jsp:include page="Header.jsp"/>

<div class="container">
    <div class="programs-header">
        <h1>Programs</h1>
        <button id="newProgramBtn" class="btn-add">+ New Program</button>
    </div>

    <div class="programs-grid">
        <c:forEach var="program" items="${programs}">
            <div class="program-card" data-name="${program.program_Name}">
                <div class="program-image">
                    <img src="${pageContext.request.contextPath}/resources/images/programs/${program.image_path}" alt="${program.program_Name}">
                </div>
                <div class="program-details">
                    <h3>${program.program_Name}</h3>
                    <p><strong>Level:</strong> ${program.program_Level}</p>
                    <p><strong>Classes:</strong> ${program.program_Classes}</p>
                    <div class="edit-button-container">
                        <button class="btn-edit" data-name="${program.program_Name}">Edit</button>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<!-- Add Program Modal -->
<div id="addProgramModal" class="modal">
    <div class="modal-content">
        <span class="close">×</span>
        <h2>Add New Program</h2>
        <form id="addProgramForm" enctype="multipart/form-data">
            <div class="form-group">
                <label for="programName">Name:</label>
                <input type="text" id="programName" name="programName" required>
            </div>
            <div class="form-group">
                <label for="programLevel">Level:</label>
                <input type="text" id="programLevel" name="programLevel" required>
            </div>
            <div class="form-group">
                <label for="programClasses">Classes:</label>
                <input type="number" id="programClasses" name="programClasses" required>
            </div>
            <div class="form-group">
                <label for="programDesc">Description:</label>
                <input type="text" id="programDesc" name="programDesc" required>
            </div>
            <div class="form-group">
                <label for="imagePath">Image:</label>
                <input type="file" id="imagePath" name="imagePath" accept="image/*" required>
            </div>
            <div class="form-buttons">
                <button type="submit" class="btn btn-submit">Add</button>
                <button type="button" class="btn btn-cancel">Cancel</button>
            </div>
        </form>
    </div>
</div>

<!-- Updated Edit Program Modal -->
<div id="editProgramModal" class="modal">
    <div class="modal-content">
        <span class="close">×</span>
        <h2>Edit Program</h2>
        <form id="editProgramForm">
            <div class="form-group">
                <label for="editName">Name:</label>
                <input type="text" id="editName" readonly>
                <!-- Hidden field for sending the program name -->
                <input type="hidden" id="hiddenProgramName" name="programName">
            </div>
            <div class="form-group">
                <label for="editLevel">Level:</label>
                <input type="text" id="editLevel" name="programLevel">
            </div>
            <div class="form-group">
                <label for="editClasses">Classes:</label>
                <input type="number" id="editClasses" name="programClasses">
            </div>
            <div class="form-group">
                <label for="editDesc">Description:</label>
                <input type="text" id="editDesc" name="programDesc">
            </div>
            <div class="form-buttons">
                <button type="submit" class="btn btn-submit">Save</button>
                <button type="button" id="deleteProgram" class="btn btn-delete">Delete</button>
                <button type="button" class="btn btn-cancel">Cancel</button>
            </div>
        </form>
    </div>
</div>

<!-- Delete Confirmation Modal -->
<div id="deleteConfirmModal" class="modal">
    <div class="modal-content">
        <span class="close">×</span>
        <h2>Confirm Delete</h2>
        <p>Are you sure you want to delete this program?</p>
        <div class="form-buttons">
            <button id="confirmDelete" class="btn btn-delete">Yes</button>
            <button id="cancelDelete" class="btn btn-cancel">No</button>
        </div>
    </div>
</div>

<jsp:include page="Footer.jsp"/>

<!-- Updated JavaScript in AdminProgram.jsp -->
<script>
document.addEventListener('DOMContentLoaded', function() {
    const addModal = document.getElementById('addProgramModal');
    const editModal = document.getElementById('editProgramModal');
    const deleteModal = document.getElementById('deleteConfirmModal');
    const newProgramBtn = document.getElementById('newProgramBtn');
    const closeBtns = document.querySelectorAll('.close');
    const cancelBtns = document.querySelectorAll('.btn-cancel');
    const addForm = document.getElementById('addProgramForm');
    const editForm = document.getElementById('editProgramForm');
    const deleteBtn = document.getElementById('deleteProgram');
    const confirmDeleteBtn = document.getElementById('confirmDelete');
    const cancelDeleteBtn = document.getElementById('cancelDelete');
    let currentProgramName = null;

    function closeAllModals() {
        addModal.style.display = 'none';
        editModal.style.display = 'none';
        deleteModal.style.display = 'none';
    }

    newProgramBtn.addEventListener('click', () => {
        console.log('New Program button clicked');
        addModal.style.display = 'flex';
    });

    closeBtns.forEach(btn => btn.addEventListener('click', closeAllModals));
    cancelBtns.forEach(btn => btn.addEventListener('click', closeAllModals));

    window.addEventListener('click', (e) => {
        if ([addModal, editModal, deleteModal].includes(e.target)) closeAllModals();
    });

    const editButtons = document.querySelectorAll('.btn-edit');
    console.log('Number of edit buttons found:', editButtons.length);
    editButtons.forEach(btn => {
        btn.addEventListener('click', (e) => {
            console.log('Edit button clicked:', btn);
            const programName = btn.getAttribute('data-name');
            if (!programName) {
                console.error('Program name not found in data-name attribute:', btn);
                return;
            }
            currentProgramName = programName;
            console.log('Current program name:', currentProgramName);
            
            // Set the program name in both visible and hidden fields
            document.getElementById('editName').value = currentProgramName;
            document.getElementById('hiddenProgramName').value = currentProgramName;
            
            fetchProgramDetails(currentProgramName)
                .then(() => {
                    console.log('Opening edit modal');
                    editModal.style.display = 'flex';
                })
                .catch(err => {
                    console.error('Error fetching program details:', err.message);
                    alert('Error: ' + err.message);
                });
        });
    });

    deleteBtn.addEventListener('click', (e) => {
        e.preventDefault();
        console.log('Delete button clicked');
        editModal.style.display = 'none';
        deleteModal.style.display = 'flex';
    });

    cancelDeleteBtn.addEventListener('click', () => {
        console.log('Cancel delete clicked');
        deleteModal.style.display = 'none';
        editModal.style.display = 'flex';
    });

    confirmDeleteBtn.addEventListener('click', () => {
        if (currentProgramName) {
            console.log('Confirm delete for program:', currentProgramName);
            deleteProgram(currentProgramName).then(() => closeAllModals()).catch(err => alert(err.message));
        } else {
            console.error('No program name set for deletion');
            alert('Error: No program selected for deletion');
        }
    });

    addForm.addEventListener('submit', (e) => {
        e.preventDefault();
        console.log('Add form submitted');
        const formData = new FormData(addForm);
        fetch('${pageContext.request.contextPath}/AddProgram', { method: 'POST', body: formData })
            .then(res => res.json())
            .then(data => {
                if (data.success) location.reload();
                else alert(data.message);
            })
            .catch(err => alert('Error adding program: ' + err));
    });

    editForm.addEventListener('submit', function(e) {
        e.preventDefault();
        console.log('Edit form submitted');
        
        // Make sure we have the program name
        if (!currentProgramName) {
            alert('Error: Program name is required');
            return;
        }
        
        // Create a URLSearchParams object for form data
        const formData = new URLSearchParams();
        
        // Add the program name first
        formData.append('programName', currentProgramName);
        
        // Add other form fields
        formData.append('programLevel', document.getElementById('editLevel').value);
        formData.append('programClasses', document.getElementById('editClasses').value);
        formData.append('programDesc', document.getElementById('editDesc').value);

        // Log the form data
        console.log('Form data being sent:');
        console.log('programName:', currentProgramName);
        console.log('programLevel:', document.getElementById('editLevel').value);
        console.log('programClasses:', document.getElementById('editClasses').value);
        console.log('programDesc:', document.getElementById('editDesc').value);

        // Send the form data using fetch
        fetch('${pageContext.request.contextPath}/EditProgram', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: formData.toString()
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            console.log('Response data:', data);
            if (data.success) {
                alert('Program updated successfully');
                location.reload();
            } else {
                alert('Error: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error updating program: ' + error.message);
        });
    });

    async function fetchProgramDetails(programName) {
        console.log('Fetching details for program:', programName);
        const url = '${pageContext.request.contextPath}/GetProgram?name=' + encodeURIComponent(programName);
        console.log('Fetch URL:', url);
        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error('Network response was not ok: ' + response.statusText);
            
            const data = await response.json();
            console.log('GetProgram response:', data);
            
            if (data.success) {
                // Set the program name in both fields
                document.getElementById('editName').value = data.program_Name || '';
                document.getElementById('hiddenProgramName').value = data.program_Name || '';
                
                document.getElementById('editLevel').value = data.program_Level || '';
                document.getElementById('editClasses').value = data.program_Classes || 0;
                document.getElementById('editDesc').value = data.program_Desc || '';

                console.log('Form values after population:', {
                    programName: document.getElementById('editName').value,
                    hiddenProgramName: document.getElementById('hiddenProgramName').value,
                    programLevel: document.getElementById('editLevel').value,
                    programClasses: document.getElementById('editClasses').value,
                    programDesc: document.getElementById('editDesc').value
                });
            } else {
                throw new Error(data.message || 'Failed to fetch program details');
            }
        } catch (err) {
            console.error('Error in fetchProgramDetails:', err);
            throw err;
        }
    }

    async function deleteProgram(programName) {
        console.log('Deleting program:', programName);
        const response = await fetch('${pageContext.request.contextPath}/DeleteProgram?name=' + encodeURIComponent(programName), { method: 'POST' });
        if (!response.ok) throw new Error('Network response was not ok: ' + response.statusText);
        const data = await response.json();
        if (!data.success) throw new Error(data.message);
        else location.reload();
    }
});
</script>

</body>
</html>