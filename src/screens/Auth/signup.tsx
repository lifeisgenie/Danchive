import { Picker } from '@react-native-picker/picker';
import axios from 'axios';
import React, { useState } from 'react';
import {
    Alert,
    ScrollView,
    StyleSheet,
    Text,
    TextInput,
    TouchableOpacity,
    View,
    ImageBackground,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import Icon from 'react-native-vector-icons/Ionicons';

const API_BASE_URL = 'http://100.84.161.55:8080/api/v1';

const validatePassword = (password) => {
    const regex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*()_+])[A-Za-z\d!@#$%^&*()_+]{8,16}$/;
    if (!password) return "비밀번호를 입력해주세요.";
    if (!regex.test(password)) return "비밀번호는 8~16자, 영문, 숫자, 특수문자를 포함해야 합니다.";
    return null;
};
const validateEmail = (email) => {
    const regex = /\S+@\S+\.\S+/;
    if (!email) return "유효한 이메일 형식이 아닙니다.";
    return null;
};

export default function SignUpPage({ navigation }) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [name, setName] = useState('');
    const [studentId, setStudentId] = useState('');
    const [department, setDepartment] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [apiError, setApiError] = useState('');
    const [validationErrors, setValidationErrors] = useState({
        email: '', password: '', name: '', department: ''
    });
    const [showPassword, setShowPassword] = useState(false);

    const validateForm = () => {
        const emailError = validateEmail(email);
        const passwordError = validatePassword(password);
        const nameError = !name ? "이름을 입력해주세요." : '';
        const departmentError = !department ? "소속/학과를 입력해주세요." : '';
        setValidationErrors({ email: emailError, password: passwordError, name: nameError, department: departmentError });
        return !emailError && !passwordError && !nameError && !departmentError;
    };

    const handleSubmit = async () => {
        if (!validateForm()) return;
        setIsLoading(true);
        setApiError('');
        try {
            const response = await axios.post(`${API_BASE_URL}/auth/register`, {
                email,
                password,
                name,
                department,
                studentId,
            });
            if (response.data.success) {
                Alert.alert("회원가입 성공", response.data.message || "로그인 페이지로 이동합니다.", [
                    { text: "확인", onPress: () => navigation.navigate('SignIn') }
                ]);
            } else {
                setApiError(response.data.message || "회원가입에 실패했습니다.");
            }
        } catch (error) {
            if (error.response && error.response.data && error.response.data.message) {
                setApiError(error.response.data.message);
            } else {
                setApiError("서버 연결에 실패했습니다. API 주소를 확인해 주세요.");
            }
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <ImageBackground
            source={require('../assets/login-bg.png')}
            style={styles.bg}
            resizeMode="cover"
        >
            <SafeAreaView style={{ flex: 1 }}>
                <ScrollView contentContainerStyle={styles.scroll}>
                    <View style={styles.card}>
                        <Text style={styles.title}>Create{"\n"}New account</Text>

                        {/* 이름 */}
                        <TextInput
                            style={[
                                styles.input,
                                !!name && styles.filledInput,
                                validationErrors.name && styles.inputError
                            ]}
                            placeholder="이름"
                            placeholderTextColor="#e7f4ff"
                            value={name}
                            onChangeText={setName}
                            editable={!isLoading}
                        />
                        {validationErrors.name ? (
                            <Text style={styles.errorText}>{validationErrors.name}</Text>
                        ) : null}

                        {/* 학번 */}
                        <TextInput
                            style={[
                                styles.input,
                                !!studentId && styles.filledInput
                            ]}
                            placeholder="학번"
                            placeholderTextColor="#e7f4ff"
                            value={studentId}
                            onChangeText={setStudentId}
                            keyboardType="numeric"
                            editable={!isLoading}
                        />

                        {/* 소속 학부 */}
                        <TextInput
                            style={[
                                styles.input,
                                !!department && styles.filledInput,
                                validationErrors.department && styles.inputError
                            ]}
                            placeholder="소속 학부"
                            placeholderTextColor="#e7f4ff"
                            value={department}
                            onChangeText={setDepartment}
                            editable={!isLoading}
                        />
                        {validationErrors.department ? (
                            <Text style={styles.errorText}>{validationErrors.department}</Text>
                        ) : null}

                        {/* 이메일 */}
                        <TextInput
                            style={[
                                styles.input,
                                !!email && styles.filledInput,
                                validationErrors.email && styles.inputError
                            ]}
                            placeholder="Email"
                            placeholderTextColor="#e7f4ff"
                            value={email}
                            onChangeText={setEmail}
                            keyboardType="email-address"
                            autoCapitalize="none"
                            editable={!isLoading}
                        />
                        {validationErrors.email ? (
                            <Text style={styles.errorText}>{validationErrors.email}</Text>
                        ) : null}

                        {/* 비밀번호 */}
                        <View style={[
                            styles.input,
                            styles.inputRow,
                            !!password && styles.filledInput,
                            validationErrors.password && styles.inputError
                        ]}>
                            <TextInput
                                style={[styles.passInput]}
                                placeholder="Password"
                                placeholderTextColor="#e7f4ff"
                                value={password}
                                onChangeText={setPassword}
                                secureTextEntry={!showPassword}
                                editable={!isLoading}
                            />
                            <TouchableOpacity style={styles.iconButton} onPress={() => setShowPassword(!showPassword)}>
                                <Icon name={showPassword ? "eye-off-outline" : "eye-outline"} size={20} color="#d7eaff" />
                            </TouchableOpacity>
                        </View>
                        {validationErrors.password ? (
                            <Text style={styles.errorText}>{validationErrors.password}</Text>
                        ) : null}

                        {apiError ? (
                            <Text style={styles.apiErrorText}>{apiError}</Text>
                        ) : null}
                        <TouchableOpacity
                            style={styles.signupButton}
                            onPress={handleSubmit}
                            disabled={isLoading}
                        >
                            <Text style={styles.signupButtonText}>
                                {isLoading ? '...' : 'sign in'}
                            </Text>
                        </TouchableOpacity>
                    </View>
                </ScrollView>
            </SafeAreaView>
        </ImageBackground>
    );
}

const styles = StyleSheet.create({
    bg: { flex: 1, width: '100%', height: '100%' },
    scroll: { flexGrow: 1, alignItems: 'center', justifyContent: 'center', paddingVertical: 20 },
    card: {
        width: '94%', maxWidth: 430,
        backgroundColor: 'rgba(34, 81, 163, 0.23)',
        borderRadius: 16,
        borderWidth: 1.2,
        borderColor: 'rgba(255,255,255,0.7)',
        paddingHorizontal: 16,
        paddingVertical: 28,
        marginTop: 24, marginBottom: 22,
        alignItems: 'stretch',
        shadowColor: "#81b7ed", shadowOffset: { width: 0, height: 14 }, shadowOpacity: 0.08, shadowRadius: 20, elevation: 9,
    },
    title: {
        fontWeight: 'bold', fontSize: 25, color: '#fff',
        textAlign: 'left', width: '100%',
        marginBottom: 12, marginLeft: 2, letterSpacing: 0.1, lineHeight: 31,
    },
    input: {
        width: '100%', height: 44, borderRadius: 7, borderWidth: 1,
        borderColor: 'rgba(255,255,255,0.52)',
        backgroundColor: 'rgba(255,255,255,0.11)',
        color: '#fff', fontSize: 15, marginVertical: 6.5, paddingHorizontal: 14,
        transition: 'background-color 0.2s'
    },
    inputRow: { flexDirection: 'row', alignItems: 'center', paddingRight: 7 },
    passInput: { flex: 1, fontSize: 15, color: '#fff', paddingVertical: 7 },
    filledInput: {
        backgroundColor: 'rgba(255,255,255,0.28)' // 더 진한 투명(값이 있을 때)
    },
    inputError: {
        borderColor: '#ef6060',
        backgroundColor: 'rgba(255,0,0,0.14)',
        color: '#fff',
    },
    iconButton: { padding: 7 },
    errorText: { color: '#ff8686', fontSize: 12, alignSelf: 'flex-start', marginLeft: 2, marginTop: 3, marginBottom: 2 },
    apiErrorText: { color: '#ffc9c9', fontSize: 13, textAlign: 'center', marginTop: 14, marginBottom: 2 },
    signupButton: {
        width: '100%', height: 48, borderRadius: 8,
        backgroundColor: 'rgba(255,255,255,0.97)', marginTop: 32,
        justifyContent: 'center', alignItems: 'center',
        shadowColor: "#486493", shadowOffset: { width: 0, height: 3 }, shadowOpacity: 0.12, shadowRadius: 6, elevation: 3,
    },
    signupButtonText: {
        color: '#1862a7', fontSize: 17, fontWeight: 'bold', letterSpacing: 0.5,
    },
});