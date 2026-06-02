<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<html>
<head>
    <title>Sign Up - My Notes</title>
    <link rel="stylesheet" href="style.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
</head>
<body class="auth-body">
<div class="auth-card">
    <div class="auth-logo">
        <i class="material-icons">person_add</i>
    </div>
    <div class="auth-title">Create an account</div>
    <div class="auth-subtitle">Start organizing your thoughts today.</div>

    <form action="login" method="post" style="margin: 0;">
        <input type="hidden" name="action" value="register">
        <div class="auth-input-group">
            <label>Username</label>
            <input type="text" name="username" placeholder="Choose a username" required>
        </div>
        <div class="auth-input-group">
            <label>Password</label>
            <input type="password" name="password" placeholder="Create a password" required>
        </div>
        <button type="submit" class="btn-auth">Create Account</button>
    </form>

    <p style="color:#DC2626; font-size:14px; font-weight:500; margin-top: 16px;">${param.err}</p>

    <a href="index.jsp" class="auth-link">Already have an account? <span>Log in</span></a>
</div>
</body>
</html>