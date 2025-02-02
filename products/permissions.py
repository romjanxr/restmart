from rest_framework import permissions

class IsReviewAuthorOrReadOnly(permissions.BasePermission):
    """
    Custom permission to allow only the review author to edit or delete their review.
    Everyone can read reviews.
    """

    def has_permission(self, request, view):
        # Allow read only access for all user
        if request.method in permissions.SAFE_METHODS:
            return True
        return request.user and request.user.is_authenticated

    def has_object_permission(self, request, view, obj):
        # allow read-only access for all users
        if request.method in permissions.SAFE_METHODS:
            return True

        # Allow full access to admin
        if request.user.is_staff:
            return True

        return obj.user == request.user