package br.edu.utfpr.dv.sireata.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.sireata.model.Ata;
import br.edu.utfpr.dv.sireata.model.Ata.TipoAta;
import br.edu.utfpr.dv.sireata.util.DateUtils;

public class AtaDAO {
	
	public Ata buscarPorId(int id) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();

		try{
			connectionDao.queryParam(
						"SELECT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
						"FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
						"INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
						"INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
						"INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
						"WHERE idAta = ?");
			
			connectionDao.setInt(1, id);
			
			ResultSet rs =  connectionDao.execute();
			
			if(rs.next()){
				return this.carregarObjeto(rs);
			}else{
				return null;
			}
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public Ata buscarPorNumero(int idOrgao, TipoAta tipo, int numero, int ano) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{
			connectionDao.queryParam(
						"SELECT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
						"FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
						"INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
						"INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
						"INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
						"WHERE atas.publicada = 1 AND atas.idOrgao = ? AND atas.tipo = ? AND atas.numero = ? AND YEAR(atas.data) = ?");
			
			connectionDao.setInt(1, idOrgao);
			connectionDao.setInt(2, tipo.getValue());
			connectionDao.setInt(3, numero);
			connectionDao.setInt(4, ano);
			
			ResultSet rs = connectionDao.execute();
			
			if(rs.next()){
				return this.carregarObjeto(rs);
			}else{
				return null;
			}
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public Ata buscarPorPauta(int idPauta) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();

		try{
			connectionDao.queryParam(
				"SELECT DISTINCT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
				"FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
				"INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
				"INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
				"INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
				"INNER JOIN pautas ON pautas.idAta=atas.idAta " +
				"WHERE pautas.idPauta = ?");
	
			connectionDao.setInt(1, idPauta);
			
			ResultSet rs = connectionDao.execute();
			
			if(rs.next()){
				return this.carregarObjeto(rs);
			}else{
				return null;
			}
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public int buscarProximoNumeroAta(int idOrgao, int ano, TipoAta tipo) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{
			connectionDao.queryParam(
				"SELECT MAX(numero) AS numero FROM atas WHERE idOrgao = ? AND YEAR(data) = ? AND tipo = ?");
		
			connectionDao.setInt(1, idOrgao);
			connectionDao.setInt(2, ano);
			connectionDao.setInt(3, tipo.getValue());
			
			ResultSet rs = connectionDao.execute();
			
			if(rs.next()){
				return rs.getInt("numero") + 1;
			}else{
				return 1;
			}
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Ata> listar(int idUsuario, int idCampus, int idDepartamento, int idOrgao, boolean publicadas) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT DISTINCT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
					"FROM atas INNER JOIN ataparticipantes ON ataparticipantes.idAta=atas.idAta " +
					"INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
					"INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
					"INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
					"INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
					"WHERE ataparticipantes.idUsuario = " + String.valueOf(idUsuario) +
					" AND atas.publicada = " + (publicadas ? "1 " : "0 ") +
					(idCampus > 0 ? " AND departamentos.idCampus = " + String.valueOf(idCampus) : "") +
					(idDepartamento > 0 ? " AND departamentos.idDepartamento = " + String.valueOf(idDepartamento) : "") +
					(idOrgao > 0 ? " AND atas.idOrgao = " + String.valueOf(idOrgao) : "") +
					"ORDER BY atas.data DESC");
			
			List<Ata> list = new ArrayList<Ata>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Ata> listarPublicadas() throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs =  connectionDao.query().executeQuery("SELECT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
				"FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
				"INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
				"INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
				"INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
				"WHERE atas.publicada=1 ORDER BY atas.data DESC");
		
			List<Ata> list = new ArrayList<Ata>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Ata> listarPorOrgao(int idOrgao) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs =  connectionDao.query().executeQuery("SELECT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
				"FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
				"INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
				"INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
				"INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
				"WHERE atas.publicada=1 AND atas.idOrgao=" + String.valueOf(idOrgao) + " ORDER BY atas.data DESC");
		
			List<Ata> list = new ArrayList<Ata>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Ata> listarPorDepartamento(int idDepartamento) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
				"FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
				"INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
				"INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
				"INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
				"WHERE atas.publicada=1 AND Orgaos.idDepartamento=" + String.valueOf(idDepartamento) + " ORDER BY atas.data DESC");
		
			List<Ata> list = new ArrayList<Ata>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Ata> listarPorCampus(int idCampus) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
				"FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
				"INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
				"INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
				"INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
				"WHERE atas.publicada=1 AND departamentos.idCampus=" + String.valueOf(idCampus) + " ORDER BY atas.data DESC");
		
			List<Ata> list = new ArrayList<Ata>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Ata> listarNaoPublicadas(int idUsuario) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT DISTINCT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
				"FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
				"INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
				"INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
				"INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
				"INNER JOIN ataparticipantes ON ataparticipantes.idAta=atas.idAta " +
				"WHERE atas.publicada=0 AND ataparticipantes.idUsuario=" + String.valueOf(idUsuario) +" ORDER BY atas.data DESC");
		
			List<Ata> list = new ArrayList<Ata>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Ata> listarPorOrgao(int idOrgao, int idUsuario) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs =  connectionDao.query().executeQuery("SELECT DISTINCT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
				"FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
				"INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
				"INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
				"INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
				"INNER JOIN ataparticipantes ON ataparticipantes.idAta=atas.idAta " +
				"WHERE atas.publicada=0 AND ataparticipantes.idUsuario=" + String.valueOf(idUsuario) + " AND atas.idOrgao=" + String.valueOf(idOrgao) + " ORDER BY atas.data DESC");
		
			List<Ata> list = new ArrayList<Ata>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Ata> listarPorDepartamento(int idDepartamento, int idUsuario) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT DISTINCT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
				"FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
				"INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
				"INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
				"INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
				"INNER JOIN ataparticipantes ON ataparticipantes.idAta=atas.idAta " +
				"WHERE atas.publicada=0 AND ataparticipantes.idUsuario=" + String.valueOf(idUsuario) + " AND Orgaos.idDepartamento=" + String.valueOf(idDepartamento) + " ORDER BY atas.data DESC");
			
			List<Ata> list = new ArrayList<Ata>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Ata> listarPorCampus(int idCampus, int idUsuario) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT DISTINCT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
				"FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
				"INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
				"INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
				"INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
				"INNER JOIN ataparticipantes ON ataparticipantes.idAta=atas.idAta " +
				"WHERE atas.publicada=0 AND ataparticipantes.idUsuario=" + String.valueOf(idUsuario) + " AND departamentos.idCampus=" + String.valueOf(idCampus) + " ORDER BY atas.data DESC");
		
			List<Ata> list = new ArrayList<Ata>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public int salvar(Ata ata) throws SQLException{
		boolean insert = (ata.getIdAta() == 0);
		
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{			
			if(insert){
				connectionDao.queryParam("INSERT INTO atas(idOrgao, idPresidente, idSecretario, tipo, numero, data, local, localCompleto, dataLimiteComentarios, consideracoesIniciais, audio, documento, publicada, dataPublicacao, aceitarComentarios) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, 0, NULL, 0)", Statement.RETURN_GENERATED_KEYS);
			}else{
				connectionDao.queryParam("UPDATE atas SET idOrgao=?, idPresidente=?, idSecretario=?, tipo=?, numero=?, data=?, local=?, localCompleto=?, dataLimiteComentarios=?, consideracoesIniciais=?, audio=? WHERE idAta=?");
			}
			
			connectionDao.setInt(1, ata.getOrgao().getIdOrgao());
			connectionDao.setInt(2, ata.getPresidente().getIdUsuario());
			connectionDao.setInt(3, ata.getSecretario().getIdUsuario());
			connectionDao.setInt(4, ata.getTipo().getValue());
			connectionDao.setInt(5, ata.getNumero());
			connectionDao.setTimestamp(6, new java.sql.Timestamp(ata.getData().getTime()));
			connectionDao.setString(7, ata.getLocal());
			connectionDao.setString(8, ata.getLocalCompleto());
			connectionDao.setDate(9, new java.sql.Date(ata.getDataLimiteComentarios().getTime()));
			connectionDao.setString(10, ata.getConsideracoesIniciais());
			
			if(ata.getAudio() == null){
				connectionDao.setInt(11, Types.BINARY);
			}else{
				connectionDao.setBytes(11, ata.getAudio());	
			}
			
			if(!insert){
				connectionDao.setInt(12, ata.getIdAta());
			}
			
			connectionDao.getPreparedStatement().execute();
			
			if(insert){
				ResultSet rs =  connectionDao.getPreparedStatement().getGeneratedKeys();
				
				if(rs.next()){
					ata.setIdAta(rs.getInt(1));
				}
			}
			
			return ata.getIdAta();
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public void publicar(int idAta, byte[] documento) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		
		try{
			connectionDao.queryParam("UPDATE atas SET documento=?, dataPublicacao=?, publicada=1, aceitarComentarios=0 WHERE publicada=0 AND idAta=?");
		
			connectionDao.setBytes(1, documento);
			connectionDao.setTimestamp(2, new java.sql.Timestamp(DateUtils.getNow().getTime().getTime()));
			connectionDao.setInt(3, idAta);
			
			connectionDao.execute();
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public void liberarComentarios(int idAta) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			connectionDao.query().executeQuery("UPDATE atas SET aceitarComentarios=1 WHERE publicada=0 AND idAta=" + String.valueOf(idAta));
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public void bloquearComentarios(int idAta) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			connectionDao.query().executeQuery("UPDATE atas SET aceitarComentarios=0 WHERE idAta=" + String.valueOf(idAta));
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	private Ata carregarObjeto(ResultSet rs) throws SQLException{
		Ata ata = new Ata();
		
		ata.setIdAta(rs.getInt("idAta"));
		ata.getOrgao().setIdOrgao(rs.getInt("idOrgao"));
		ata.getOrgao().setNome(rs.getString("orgao"));
		ata.getPresidente().setIdUsuario(rs.getInt("idPresidente"));
		ata.getPresidente().setNome(rs.getString("presidente"));
		ata.getSecretario().setIdUsuario(rs.getInt("idSecretario"));
		ata.getSecretario().setNome(rs.getString("secretario"));
		ata.setTipo(TipoAta.valueOf(rs.getInt("tipo")));
		ata.setNumero(rs.getInt("numero"));
		ata.setData(rs.getTimestamp("data"));
		ata.setLocal(rs.getString("local"));
		ata.setLocalCompleto(rs.getString("localCompleto"));
		ata.setDataLimiteComentarios(rs.getDate("dataLimiteComentarios"));
		ata.setConsideracoesIniciais(rs.getString("consideracoesIniciais"));
		ata.setAudio(rs.getBytes("audio"));
		ata.setPublicada(rs.getInt("publicada") == 1);
		ata.setAceitarComentarios(rs.getInt("aceitarComentarios") == 1);
		ata.setDataPublicacao(rs.getTimestamp("dataPublicacao"));
		ata.setDocumento(rs.getBytes("documento"));
		
		return ata;
	}
	
	public boolean temComentarios(int idAta) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs =  connectionDao.query().executeQuery("SELECT COUNT(comentarios.idComentario) AS qtde FROM comentarios " +
				"INNER JOIN pautas ON pautas.idPauta=comentarios.idPauta " + 
				"WHERE pautas.idAta=" + String.valueOf(idAta));
		
			if(rs.next()){
				return (rs.getInt("qtde") > 0);
			}else{
				return false;
			}
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public boolean isPresidenteOuSecretario(int idUsuario, int idAta) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT atas.idAta FROM atas " +
				"WHERE idAta=" + String.valueOf(idAta) + " AND (idPresidente=" + String.valueOf(idUsuario) + " OR idSecretario=" + String.valueOf(idUsuario) + ")");
		
			return rs.next();
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public boolean isPresidente(int idUsuario, int idAta) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT atas.idAta FROM atas " +
				"WHERE idAta=" + String.valueOf(idAta) + " AND idPresidente=" + String.valueOf(idUsuario));
		
			return rs.next();
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public boolean isPublicada(int idAta) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT atas.publicada FROM atas " +
				"WHERE idAta=" + String.valueOf(idAta));
		
			if(rs.next()) {
				return rs.getInt("publicada") == 1;
			} else {
				return false;
			}
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public boolean excluir(int idAta) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{
			connectionDao.getConnection().setAutoCommit(false);
			
			connectionDao.query().executeQuery("DELETE FROM comentarios WHERE idPauta IN (SELECT idPauta FROM pautas WHERE idAta=" + String.valueOf(idAta) + ")");
			connectionDao.query().executeQuery("DELETE FROM pautas WHERE idAta=" + String.valueOf(idAta));
			connectionDao.query().executeQuery("DELETE FROM ataparticipantes WHERE idAta=" + String.valueOf(idAta));
			connectionDao.query().executeQuery("DELETE FROM anexos WHERE idAta=" + String.valueOf(idAta));
			boolean ret = connectionDao.getStatement().execute("DELETE FROM atas WHERE idAta=" + String.valueOf(idAta));
			
			connectionDao.getConnection().commit();
			
			return ret;
		}catch(SQLException ex) {
			connectionDao.getConnection().rollback();
			throw ex;
		}
	}
}
