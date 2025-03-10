from djoser.serializers import UserCreateSerializer as BaseUserCreateSerializer, UserSerializer as BaseUserSerializer


class UserCreateSerializer(BaseUserCreateSerializer):
    """Used for user registration"""
    class Meta(BaseUserCreateSerializer.Meta):
        fields = ['id',  'email', 'password', 'first_name',
                  'last_name', 'address', 'phone_number']


class UserSerializer(BaseUserSerializer):
    """Used for retrieving and updating user details"""
    class Meta(BaseUserSerializer.Meta):
        fields = ['id',  'email', 'first_name',
                  'last_name', 'address', 'phone_number', 'is_staff']
        # Prevent users from modifying these fields
        read_only_fields = ['email', 'is_staff']
