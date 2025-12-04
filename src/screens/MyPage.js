import React, { useEffect, useState } from 'react';
import {
  Modal,
  TextInput,
  Alert,
  View,
  Text,
  StyleSheet,
  Image,
  TouchableOpacity,
  ScrollView,
  Touchable,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';

const API_BASE_URL = 'http://100.84.161.55:8080/api/v1';

const menuItems = [
  { title: '비밀번호 재설정' },
  { title: '회원탈퇴' },
];

export default function MyPage({ navigation }) {
  const [userInfo, setUserInfo] = useState(null);
  const [showTeamModal, setShowTeamModal] = useState(false);
  const [teamName, setTeamName] = useState('');
  const [showInviteModal, setShowInviteModal] = useState(false);
  const [inviteEmail, setInviteEmail] = useState('');
  const [isInviting, setIsInviting] = useState(false);
  const [teamInfo, setTeamInfo] = useState(null);   // 팀(멤버 등) 정보 별도 저장
  const [showInviteList, setShowInviteList] = useState(false);
  const [inviteList, setInviteList] = useState([]);
  const [selectedInviteId, setSelectedInviteId] = useState(null);
  const [isAcceptingInvite, setIsAcceptingInvite] = useState(false);
  // 작품 목록 조회 함수 추가
  const fetchProjectTitle = async (teamName) => {
    if (!teamName) return null;
    try {
      const resp = await axios.get(`${API_BASE_URL.replace('/api/v1', '/api/v1/exhibits')}?teamName=${encodeURIComponent(teamName)}`);
      // 위 URL은 실제 서버 라우팅에 맞게 수정 필요
      if (resp.data?.success && Array.isArray(resp.data.data.content)) {
        const myTeamProject = resp.data.data.content.find(
          (p) => p.teamName === teamName
        );
        return myTeamProject ? myTeamProject.title : null;
      }
    } catch (e) {
      console.log('작품 조회 에러:', e);
    }
    return null;
  };

  const fetchUserAndTeamInfo = async () => {
    const accessToken = await AsyncStorage.getItem('userToken');
    if (!accessToken) return;

    try {
      const userStr = await AsyncStorage.getItem('currentUser');
      let prevUser = {};
      if (userStr) {
        try {
          prevUser = JSON.parse(userStr);
          setUserInfo(prevUser);
        } catch (e) { }
      }

      const resp = await axios.get(`${API_BASE_URL}/teams/me`, {
        headers: { Authorization: `Bearer ${accessToken}` }
      });

      if (resp.data?.success && resp.data?.data) {
        const teamData = resp.data.data;
        setTeamInfo(teamData);

        let role = null;
        if (prevUser.userId && Array.isArray(teamData.members)) {
          const myinfo = teamData.members.find(m => m.userId === prevUser.userId);
          if (myinfo) role = myinfo.roleInTeam || null;
        } else if (prevUser.name) {
          const myinfo = teamData.members.find(m => m.name === prevUser.name);
          if (myinfo) role = myinfo.roleInTeam || null;
        }

        // 작품명 추가 fetch
        let projectTitle = null;
        if (teamData.teamName) {
          projectTitle = await fetchProjectTitle(teamData.teamName);
        }

        const merged = {
          ...prevUser,
          team: teamData.teamName,
          teamId: teamData.teamId,
          roleInTeam: role,
          projectTitle,
        };

        setUserInfo(merged);
        await AsyncStorage.setItem('currentUser', JSON.stringify(merged));
      } else {
        setTeamInfo(null);
        setUserInfo({ ...prevUser, team: null, teamId: null, roleInTeam: null, projectTitle: null });
        await AsyncStorage.setItem('currentUser', JSON.stringify({ ...prevUser, team: null, teamId: null, roleInTeam: null, projectTitle: null }));
      }
    } catch (e) {
      console.log('팀 정보 조회 에러:', e);
      setTeamInfo(null);
    }
  };




  useEffect(() => {
    fetchUserAndTeamInfo();
  }, []);

  // === 작품 등록 ===
  const handleRegisterProject = async () => {
    if (!userInfo?.roleInTeam || userInfo.roleInTeam !== 'leader') {
      Alert.alert('권한 없음', '작품 등록은 팀장만 할 수 있습니다.');
      return;
    }
    if (!teamInfo?.teamId) {
      Alert.alert('오류', '팀 정보가 필요합니다.');
      return;
    }
    // 바로 등록 페이지로 이동 (서버에서 중복등록 시도 시 안내 메시지 제공)
    navigation.navigate('ProjectRegister', { teamId: teamInfo.teamId });
  };


  // === 팀 생성 ===
  const handleCreateTeam = () => setShowTeamModal(true);

  const submitCreateTeam = async () => {
    if (!teamName.trim()) {
      Alert.alert('알림', '팀 이름을 입력해 주세요.');
      return;
    }

    if (userInfo?.teamId || userInfo?.team) {
      Alert.alert('알림', '이미 팀에 소속되어 있습니다.');
      return;
    }

    try {
      const accessToken = await AsyncStorage.getItem('userToken');
      if (!accessToken) {
        Alert.alert('오류', '로그인이 필요합니다.');
        return;
      }

      const response = await axios.post(
        `${API_BASE_URL}/teams`,
        { teamName },
        { headers: { Authorization: `Bearer ${accessToken}` } }
      );

      if (response.data?.success && response.data?.data) {
        const { teamId, teamName: newTeamName } = response.data.data;
        const updatedUserInfo = {
          ...(userInfo || {}),
          team: newTeamName,
          teamId,
          roleInTeam: 'leader',
        };
        setUserInfo(updatedUserInfo);
        await AsyncStorage.setItem('currentUser', JSON.stringify(updatedUserInfo));
        fetchUserAndTeamInfo();
        setShowTeamModal(false);
        setTeamName('');
      }
      else {
        Alert.alert('실패', response.data?.message || '팀 생성에 실패했습니다.');
      }
    } catch (e) {
      console.log('팀 생성 에러:', e);
      if (e.response) {
        Alert.alert('오류', e.response.data?.message || '서버 응답 오류');
      } else if (e.request) {
        Alert.alert('네트워크 오류', '요청은 전송됐지만 응답이 없습니다.');
      } else {
        Alert.alert('환경 오류', e.message);
      }
    }
  };

  // === 팀 초대 ===
  const handleInviteMember = () => setShowInviteModal(true);

  const submitInviteMember = async () => {
    setIsInviting(true);
    try {
      const accessToken = await AsyncStorage.getItem('userToken');
      if (!accessToken) {
        Alert.alert('오류', '로그인이 필요합니다.');
        setIsInviting(false);
        return;
      }

      const teamId = userInfo?.teamId;
      if (!teamId) {
        Alert.alert('오류', '팀이 없습니다. 먼저 팀을 생성해 주세요.');
        setIsInviting(false);
        return;
      }

      const response = await axios.post(
        `${API_BASE_URL}/teams/${teamId}/invites`,
        { email: inviteEmail },
        { headers: { Authorization: `Bearer ${accessToken}` } }
      );

      if (response.data?.success) {
        Alert.alert('성공', response.data.message || '초대가 전송되었습니다.');
        setShowInviteModal(false);
        setInviteEmail('');
      } else {
        Alert.alert('실패', response.data?.message || '초대에 실패했습니다.');
      }
    } catch (e) {
      console.log('초대 에러:', e);
      Alert.alert('오류', '네트워크 또는 서버 오류입니다.');
    } finally {
      setIsInviting(false);
    }
  };

  // === 초대 목록 불러오기 ===
  const fetchInviteList = async () => {
    try {
      const accessToken = await AsyncStorage.getItem('userToken');
      const resp = await axios.get(`${API_BASE_URL}/teams/invites/me`, {
        headers: { Authorization: `Bearer ${accessToken}` }
      });
      if (resp.data?.success && Array.isArray(resp.data.data)) {
        setInviteList(resp.data.data);
      } else {
        setInviteList([]);
      }
    } catch (e) {
      setInviteList([]);
      Alert.alert('초대 현황 오류', '초대 조회 중 네트워크 오류가 발생했습니다.');
    }
  };
  const handleInviteStatus = async () => {
    await fetchInviteList();
    setShowInviteList(true);
  };

  // === 초대 수락 ===
  const acceptInvite = async () => {
    if (!selectedInviteId) {
      Alert.alert('선택 없음', '먼저 초대를 선택하세요.');
      return;
    }
    setIsAcceptingInvite(true);
    try {
      const accessToken = await AsyncStorage.getItem('userToken');
      const resp = await axios.post(
        `${API_BASE_URL}/teams/invites/${selectedInviteId}/accept`,
        {},
        { headers: { Authorization: `Bearer ${accessToken}` } }
      );
      if (resp.data?.success) {
        Alert.alert('수락 완료', resp.data.message || '초대를 수락했습니다.');
        setShowInviteList(false);
        setSelectedInviteId(null);

        // 역할을 member로 할당
        if (userInfo) {
          const updatedUserInfo = {
            ...userInfo,
            roleInTeam: 'member',
          };
          setUserInfo(updatedUserInfo);
          await AsyncStorage.setItem('currentUser', JSON.stringify(updatedUserInfo));
        }

        // 필요 시 팀 정보 리로드 등 추가
        fetchUserAndTeamInfo();
      } else {
        Alert.alert('실패', resp.data?.message || '초대 수락에 실패했습니다.');
      }
    } catch (e) {
      Alert.alert('오류', '네트워크 또는 서버 오류입니다.');
    } finally {
      setIsAcceptingInvite(false);
    }
  };


  const handleLogout = async () => {
    try {
      console.log('[로그아웃] 버튼 누름');
      const accessToken = await AsyncStorage.getItem('userToken');
      console.log('[로그아웃] 토큰:', accessToken);
      if (!accessToken) {
        Alert.alert('로그아웃 실패', '액세스 토큰이 없습니다.');
        navigation.reset({
          index: 0,
          routes: [{ name: 'Auth' }],
        });
        return;
      }
      await axios.post(
        `${API_BASE_URL}/auth/logout`,
        {},
        { headers: { Authorization: `Bearer ${accessToken}` } }
      );
      await AsyncStorage.multiRemove(['userToken', 'refreshToken', 'currentUser']);
      console.log('[로그아웃] API 호출 및 토큰 삭제 완료');
      Alert.alert('로그아웃 완료', '정상적으로 로그아웃되었습니다.', [
        {
          text: '확인',
          onPress: () => {
            console.log('[로그아웃] navigation.reset 시도');
            navigation.reset({
              index: 0,
              routes: [{ name: 'Auth' }],
            });
          }
        }
      ]);
    } catch (e) {
      console.log('Logout Error:', e);  // 이 로그 반드시 체크!
      Alert.alert('로그아웃 실패', '서버와 통신에 실패했습니다.');
      navigation.reset({
        index: 0,
        routes: [{ name: 'Auth' }],
      })
    }
  };


  return (
    <SafeAreaView style={styles.container}>

      <Modal
        visible={showTeamModal}
        animationType="slide"
        transparent
        onRequestClose={() => setShowTeamModal(false)}
      >
        <View style={{
          flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: 'rgba(0,0,0,0.3)'
        }}>
          <View style={{
            width: 300, backgroundColor: 'white', borderRadius: 10, padding: 20, alignItems: 'center'
          }}>
            <Text style={{ fontSize: 16, fontWeight: 'bold', marginBottom: 14 }}>팀 이름을 입력하세요</Text>
            <TextInput
              style={{
                borderWidth: 1, borderColor: '#ccc', borderRadius: 6, width: '100%',
                padding: 10, marginBottom: 20
              }}
              placeholder="팀 이름"
              value={teamName}
              onChangeText={setTeamName}
            />
            <TouchableOpacity
              style={{ backgroundColor: '#023560', paddingHorizontal: 24, paddingVertical: 10, borderRadius: 6, marginBottom: 10 }}
              onPress={submitCreateTeam}
            >
              <Text style={{ color: 'white', fontWeight: 'bold' }}>팀 생성</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={{ padding: 8 }}
              onPress={() => setShowTeamModal(false)}
            >
              <Text style={{ color: 'gray' }}>닫기</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>

      <Modal
        visible={showInviteModal}
        animationType="slide"
        transparent
        onRequestClose={() => setShowInviteModal(false)}
      >
        <View style={{
          flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: 'rgba(0,0,0,0.3)'
        }}>
          <View style={{
            width: 300, backgroundColor: 'white', borderRadius: 10, padding: 20, alignItems: 'center'
          }}>
            <Text style={{ fontSize: 16, fontWeight: 'bold', marginBottom: 14 }}>초대할 팀원 이메일</Text>
            <TextInput
              style={{
                borderWidth: 1, borderColor: '#ccc', borderRadius: 6, width: '100%',
                padding: 10, marginBottom: 20
              }}
              placeholder="이메일"
              value={inviteEmail}
              onChangeText={setInviteEmail}
              keyboardType="email-address"
              autoCapitalize="none"
            />
            <TouchableOpacity
              style={{ backgroundColor: '#023560', paddingHorizontal: 24, paddingVertical: 10, borderRadius: 6, marginBottom: 10 }}
              onPress={submitInviteMember}
              disabled={isInviting}
            >
              <Text style={{ color: 'white', fontWeight: 'bold' }}>초대</Text>
            </TouchableOpacity>
            <TouchableOpacity style={{ padding: 8 }} onPress={() => setShowInviteModal(false)}>
              <Text style={{ color: 'gray' }}>닫기</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>

      <Modal
        visible={showInviteList}
        transparent
        animationType="slide"
        onRequestClose={() => setShowInviteList(false)}
      >
        <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: 'rgba(0,0,0,0.2)' }}>
          <View style={{ width: 320, backgroundColor: 'white', borderRadius: 10, padding: 24 }}>
            <Text style={{ fontWeight: 'bold', marginBottom: 14 }}>내게 온 팀 초대 현황</Text>
            {inviteList.length === 0 ? (
              <Text style={{ marginBottom: 18 }}>받은 초대가 없습니다.</Text>
            ) : (
              inviteList.map(invite => (
                <TouchableOpacity
                  key={invite.inviteId}
                  style={{
                    borderWidth: 1,
                    borderColor: selectedInviteId === invite.inviteId ? '#023560' : '#888',
                    borderRadius: 8,
                    padding: 12,
                    marginBottom: 10,
                    backgroundColor: selectedInviteId === invite.inviteId ? '#e8f2ff' : '#f8f9fa',
                  }}
                  onPress={() => setSelectedInviteId(invite.inviteId)}
                >
                  <Text style={{ fontWeight: '600' }}>{invite.teamName}</Text>
                  <Text style={{ fontSize: 12, color: '#555' }}>상태: 수락 대기 중</Text>
                  <Text style={{ fontSize: 12, color: '#999' }}>초대일: {invite.invitedAt?.slice(0, 10)}</Text>
                </TouchableOpacity>
              ))
            )}
            <TouchableOpacity
              style={{
                marginTop: 10,
                backgroundColor: selectedInviteId ? '#023560' : '#b8c6cd',
                borderRadius: 6,
                paddingVertical: 10,
                alignItems: 'center'
              }}
              onPress={acceptInvite}
              disabled={!selectedInviteId || isAcceptingInvite}
            >
              <Text style={{ color: 'white', fontWeight: 'bold' }}>초대 수락</Text>
            </TouchableOpacity>
            <TouchableOpacity style={{ marginTop: 12, alignItems: 'center' }} onPress={() => setShowInviteList(false)}>
              <Text style={{ color: '#555' }}>닫기</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>

      <ScrollView contentContainerStyle={{
        flexGrow: 1,
        paddingBottom: 60,
        backgroundColor: '#fff'
      }}
        showsVerticalScrollIndicator={false}>
        <View style={styles.logoWrapper}>
          <Image source={require('./assets/logo.png')} style={styles.logo} />
        </View>

        <View style={styles.profileRow}>
          <Image
            source={
              userInfo && userInfo.profile
                ? userInfo.profile
                : require('./assets/profile.png') // 기본 이미지
            }
            style={styles.profileImage}
          />
          <View style={styles.infoSection}>
            <View style={styles.infoRow}>
              <Text style={styles.label}>이름</Text>
              <Text style={styles.value}>
                {userInfo && userInfo.name ? userInfo.name : 'guest'}
              </Text>
            </View>
            <View style={styles.infoRow}>
              <Text style={styles.label}>팀 이름</Text>
              <Text style={styles.value}>
                {teamInfo?.teamName ? teamInfo.teamName : '정보 없음'}
              </Text>
            </View>
            <View style={styles.infoRow}>
              <Text style={styles.label}>팀원</Text>
              <View style={{ flex: 1 }}>
                {teamInfo?.members && teamInfo.members.length > 0 ? (
                  teamInfo.members.map((m, idx) => (
                    <Text key={idx} style={styles.value}>
                      {m.name}
                    </Text>
                  ))
                ) : (
                  <Text style={styles.value}>정보 없음</Text>
                )}
              </View>
            </View>
            <View style={styles.infoRow}>
              <Text style={styles.label}>역할</Text>
              <Text style={styles.value}>
                {
                  userInfo && typeof userInfo.roleInTeam === 'string' && userInfo.roleInTeam.length > 0
                    ? userInfo.roleInTeam === 'leader'
                      ? '팀장'
                      : userInfo.roleInTeam === 'member'
                        ? '팀원'
                        : userInfo.roleInTeam
                    : '정보 없음'
                }
              </Text>
            </View>
            <View style={styles.infoRow}>
              <Text style={styles.label}>작품</Text>
              <Text style={styles.value}>
                {userInfo && userInfo.projectTitle ? userInfo.projectTitle : '정보 없음'}
              </Text>
            </View>
          </View>


        </View>
        <View style={styles.menuSection}>
          <TouchableOpacity style={styles.menuItem} onPress={handleCreateTeam}>
            <Text style={styles.menuText}>팀 생성</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.menuItem} onPress={handleInviteMember}>
            <Text style={styles.menuText}>팀원 초대</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.menuItem} onPress={handleRegisterProject}>
            <Text style={styles.menuText}>작품 등록</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.menuItem} onPress={handleInviteStatus}>
            <Text style={styles.menuText}>초대 현황</Text>
          </TouchableOpacity>
          {menuItems.map((item, idx) => (
            <TouchableOpacity key={idx} style={styles.menuItem}>
              <Text style={styles.menuText}>{item.title}</Text>
            </TouchableOpacity>
          ))}

          {/* 로그아웃 */}
          <TouchableOpacity
            style={styles.menuItem}
            onPress={handleLogout}
          >
            <Text style={[styles.menuText, { color: '#e63a3a', fontWeight: 'bold' }]}>
              로그아웃
            </Text>
          </TouchableOpacity>

        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#fff' },
  logoWrapper: { marginTop: 12, marginLeft: 16, marginBottom: 18 },
  logo: { width: 100, height: 25, marginTop: 10, resizeMode: 'contain' },
  profileRow: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    paddingHorizontal: 28,
    marginBottom: 8,
  },
  profileImage: {
    width: 90,
    height: 110,
    borderRadius: 12,
    marginRight: 24,
    marginTop: 40,
  },
  infoSection: { flex: 1 },
  infoRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 18,
    borderBottomWidth: 1,
    borderColor: '#eee',
    paddingBottom: 2,
  },
  label: { width: 80, fontSize: 16, color: '#777' },
  value: { fontSize: 16, color: '#222', flex: 1 },
  menuSection: { marginTop: 28, paddingHorizontal: 28 },
  menuItem: {
    paddingVertical: 16,
    borderBottomWidth: 1,
    borderColor: '#eee',
  },
  menuText: { fontSize: 16, color: '#444' },


});

